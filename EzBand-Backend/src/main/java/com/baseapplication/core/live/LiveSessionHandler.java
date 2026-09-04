package com.baseapplication.core.live;

import com.baseapplication.core.dto.live.LiveClientMessage;
import com.baseapplication.core.dto.live.LiveMembroPresenca;
import com.baseapplication.core.dto.live.LiveServerMessage;
import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import com.baseapplication.core.service.LiveSessionService;
import com.baseapplication.core.service.SessaoAoVivoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Set;

/**
 * O endpoint da sessão ao vivo.
 *
 * <p>Roteia comandos, transmite mutações e mantém a presença. Não decide nada: as regras
 * moram no {@link LiveSessionService}, e o transporte no {@link LiveSessionRegistry}.
 *
 * <p>Não há {@code @Transactional} em lugar nenhum deste caminho, nem acesso a JPA fora da
 * abertura da sessão. O socket trafega índices; o repertório o cliente já carregou por REST.
 */
@Slf4j
@Component
public class LiveSessionHandler extends TextWebSocketHandler {

    /** Teto para o buffer de saída de um cliente lento antes de derrubar a conexão dele. */
    private static final int LIMITE_BUFFER_BYTES = 512 * 1024;
    private static final int LIMITE_ENVIO_MS = 10_000;

    /** Sem ninguém conectado por este tempo, a sessão é dada como acabada e vira resumo. */
    private static final long ABANDONO_MS = 30 * 60 * 1000L;

    private final LiveSessionService liveSessionService;
    private final SessaoAoVivoService sessaoAoVivoService;
    private final LiveSessionRegistry registry;
    private final LiveSessionStore store;
    private final ObjectMapper mapper;

    public LiveSessionHandler(LiveSessionService liveSessionService,
                              SessaoAoVivoService sessaoAoVivoService,
                              LiveSessionRegistry registry,
                              LiveSessionStore store,
                              ObjectMapper mapper) {
        this.liveSessionService = liveSessionService;
        this.sessaoAoVivoService = sessaoAoVivoService;
        this.registry = registry;
        this.store = store;
        this.mapper = mapper;
    }

    // ── Ciclo de vida ───────────────────────────────────────────────────

    @Override
    public void afterConnectionEstablished(WebSocketSession sessaoCrua) {
        WebSocketSession sessao = new ConcurrentWebSocketSessionDecorator(
                sessaoCrua, LIMITE_ENVIO_MS, LIMITE_BUFFER_BYTES);

        LiveUsuarioSessao usuario = registry.usuarioDe(sessao);
        if (usuario == null) {
            fechar(sessao, CloseStatus.POLICY_VIOLATION);
            return;
        }

        registry.entrar(usuario.getRoomKey(), sessao);

        LiveSessionSnapshot snapshot;
        try {
            snapshot = liveSessionService.abrirOuEntrar(usuario);
        } catch (Exception e) {
            log.error("Falha ao abrir sessão ao vivo em {}", usuario.getRoomKey(), e);
            registry.enviar(sessao, LiveServerMessage.erro("FALHA_ABERTURA",
                    "Não foi possível abrir a sessão ao vivo agora."));
            fechar(sessao, CloseStatus.SERVER_ERROR);
            return;
        }

        // Quem chega recebe o estado completo antes de qualquer outra coisa: é o snapshot que
        // diz em que música a banda está, não o histórico de eventos que ele perdeu.
        registry.enviar(sessao, LiveServerMessage.sessionState(snapshot, presencaDe(usuario, snapshot)));

        LiveSessionSnapshot aposControle = liveSessionService.reavaliarControle(
                usuario.getRoomKey(), registry.idsUsuariosConectados(usuario.getRoomKey()));
        if (aposControle != null) {
            registry.broadcast(usuario.getRoomKey(), LiveServerMessage.controllerChanged(aposControle, null));
            snapshot = aposControle;
        }

        anunciarPresenca(usuario, snapshot, true);
        log.debug("Usuário {} entrou na sessão ao vivo {}", usuario.getIdUsuario(), usuario.getRoomKey());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession sessao, CloseStatus status) {
        LiveUsuarioSessao usuario = registry.usuarioDe(sessao);
        if (usuario == null) return;

        boolean salaVazia = registry.sair(usuario.getRoomKey(), sessao.getId());
        if (salaVazia) {
            // A sessão continua no store: a banda pode ter fechado tudo por causa de um
            // roteador que caiu, e o TTL cobre o resto. O varredor decide o encerramento.
            log.debug("Sessão ao vivo {} ficou sem conexões", usuario.getRoomKey());
            return;
        }

        LiveSessionSnapshot snapshot = liveSessionService.reavaliarControle(
                usuario.getRoomKey(), registry.idsUsuariosConectados(usuario.getRoomKey()));
        if (snapshot != null) {
            registry.broadcast(usuario.getRoomKey(), LiveServerMessage.controllerChanged(snapshot, null));
        } else {
            snapshot = liveSessionService.buscar(usuario.getRoomKey());
        }

        anunciarPresenca(usuario, snapshot, false);
    }

    @Override
    public void handleTransportError(WebSocketSession sessao, Throwable exception) {
        log.debug("Erro de transporte na sessão {}: {}", sessao.getId(), exception.getMessage());
    }

    // ── Comandos ────────────────────────────────────────────────────────

    @Override
    protected void handleTextMessage(WebSocketSession sessaoCrua, TextMessage message) {
        LiveUsuarioSessao usuario = registry.usuarioDe(sessaoCrua);
        if (usuario == null) return;

        // Responder pela sessão registrada, não pela crua: só a registrada tem o decorator
        // que serializa os envios concorrentes.
        WebSocketSession sessao = registry.sessoes(usuario.getRoomKey()).stream()
                .filter(s -> s.getId().equals(sessaoCrua.getId()))
                .findFirst()
                .orElse(sessaoCrua);

        LiveClientMessage comando;
        try {
            comando = mapper.readValue(message.getPayload(), LiveClientMessage.class);
        } catch (Exception e) {
            registry.enviar(sessao, LiveServerMessage.erro("MENSAGEM_INVALIDA", "Comando não reconhecido."));
            return;
        }
        if (comando.getTipo() == null) return;

        try {
            despachar(comando, usuario, sessao);
        } catch (LiveComandoInvalidoException e) {
            // Recusa não derruba o socket: no meio de um show, fechar a conexão de alguém por
            // causa de um toque inválido é o pior desfecho possível.
            registry.enviar(sessao, LiveServerMessage.erro(e.getCodigo(), e.getMessage()));
        } catch (Exception e) {
            log.error("Falha ao processar {} na sessão {}", comando.getTipo(), usuario.getRoomKey(), e);
            registry.enviar(sessao, LiveServerMessage.erro("FALHA_INTERNA", "Não foi possível executar o comando."));
        }
    }

    private void despachar(LiveClientMessage comando, LiveUsuarioSessao usuario, WebSocketSession sessao) {
        String roomKey = usuario.getRoomKey();

        switch (comando.getTipo()) {
            case SYNC -> {
                LiveSessionSnapshot snapshot = liveSessionService.buscar(roomKey);
                if (snapshot == null) throw LiveComandoInvalidoException.semSessao();
                registry.enviar(sessao, LiveServerMessage.sessionState(snapshot, presencaDe(usuario, snapshot)));
            }
            case SET_TRACK -> transmitirTroca(liveSessionService.definirFaixa(usuario, comando.getIdx()), usuario);
            case NEXT_TRACK -> transmitirTroca(liveSessionService.moverFaixa(usuario, 1), usuario);
            case PREV_TRACK -> transmitirTroca(liveSessionService.moverFaixa(usuario, -1), usuario);
            case BACK_TO_SETLIST -> transmitirTroca(liveSessionService.voltarAoSetlist(usuario), usuario);
            case CLAIM_CONTROL -> transmitirControle(liveSessionService.assumirControle(usuario), usuario);
            case RELEASE_CONTROL -> transmitirControle(liveSessionService.liberarControle(usuario), usuario);
            case TIMER_START -> transmitirTimer(liveSessionService.iniciarTimer(usuario, comando.getDuracaoSegundos()), usuario);
            case TIMER_PAUSE -> transmitirTimer(liveSessionService.pausarTimer(usuario), usuario);
            case TIMER_RESET -> transmitirTimer(liveSessionService.zerarTimer(usuario), usuario);
            case CUE -> registry.broadcast(roomKey, LiveServerMessage.cue(
                    usuario.getIdUsuario(), usuario.getNome(), comando.getCueTipo(), comando.getCueTexto()));
            case END_SESSION -> encerrar(usuario);
            default -> registry.enviar(sessao, LiveServerMessage.erro("COMANDO_DESCONHECIDO",
                    "Comando ainda não suportado nesta versão."));
        }
    }

    private void encerrar(LiveUsuarioSessao usuario) {
        LiveSessionSnapshot snapshot = liveSessionService.encerrar(usuario);
        // Gravação síncrona: é um comando por show, não o caminho quente. A restrição herdada
        // do incidente do Hikari é não *segurar* conexão numa sessão longa, não nunca escrever.
        Long idResumo = sessaoAoVivoService.persistir(snapshot);
        registry.broadcast(usuario.getRoomKey(), LiveServerMessage.sessionEnded(idResumo, usuario.getIdUsuario()));
    }

    private void transmitirTimer(LiveSessionSnapshot snapshot, LiveUsuarioSessao usuario) {
        if (snapshot == null) return;
        registry.broadcast(usuario.getRoomKey(), LiveServerMessage.timerChanged(snapshot, usuario.getIdUsuario()));
    }

    private void transmitirTroca(LiveSessionSnapshot snapshot, LiveUsuarioSessao usuario) {
        if (snapshot == null) return; // comando não mudou nada — nada a transmitir
        registry.broadcast(usuario.getRoomKey(), LiveServerMessage.trackChanged(snapshot, usuario.getIdUsuario()));
    }

    private void transmitirControle(LiveSessionSnapshot snapshot, LiveUsuarioSessao usuario) {
        if (snapshot == null) return;
        registry.broadcast(usuario.getRoomKey(), LiveServerMessage.controllerChanged(snapshot, usuario.getIdUsuario()));
        // A presença carrega quem é controlador, então ela também precisa ser reemitida.
        anunciarPresenca(usuario, snapshot, null);
    }

    // ── Presença e heartbeat ────────────────────────────────────────────

    private List<LiveMembroPresenca> presencaDe(LiveUsuarioSessao usuario, LiveSessionSnapshot snapshot) {
        return registry.presenca(usuario.getRoomKey(),
                snapshot == null ? List.of() : snapshot.getControladores());
    }

    private void anunciarPresenca(LiveUsuarioSessao usuario, LiveSessionSnapshot snapshot, Boolean entrou) {
        List<LiveMembroPresenca> presenca = presencaDe(usuario, snapshot);
        long seq = snapshot == null ? 0L : snapshot.getSeq();
        registry.broadcast(usuario.getRoomKey(), LiveServerMessage.presenceChanged(
                seq, entrou == null ? null : usuario.toPresenca(), Boolean.TRUE.equals(entrou), presenca));
    }

    /**
     * Ping a cada 25s em todas as salas.
     *
     * <p>Existe para o caso do celular que perdeu sinal sem fechar o socket: sem tráfego, o
     * container só descobriria na próxima escrita, e a presença mostraria alguém no palco que
     * já foi embora. Também mantém proxies intermediários de fecharem a conexão por ociosidade.
     */
    @Scheduled(fixedRate = 25_000)
    public void heartbeat() {
        Set<String> salas = registry.salasComConexao();
        for (String roomKey : salas) {
            registry.ping(roomKey);
        }
    }

    /**
     * Fecha sessões que ninguém encerrou.
     *
     * <p>O caso comum não é a banda apertar "encerrar": é todo mundo guardar o celular quando
     * o show acaba. Sem este varredor o resumo pós-show nunca seria gravado, e o snapshot
     * ficaria no Redis até o TTL expirar levando o histórico junto.
     */
    @Scheduled(fixedRate = 10 * 60_000)
    public void varrerSessoesAbandonadas() {
        long agora = System.currentTimeMillis();
        for (String roomKey : store.salasAtivas()) {
            if (!registry.sessoes(roomKey).isEmpty()) continue;

            LiveSessionSnapshot snapshot = liveSessionService.buscar(roomKey);
            if (snapshot == null) continue;

            long ultimaAtividade = snapshot.getAtualizadaEm() != null
                    ? snapshot.getAtualizadaEm()
                    : (snapshot.getIniciadaEm() != null ? snapshot.getIniciadaEm() : agora);
            if (agora - ultimaAtividade < ABANDONO_MS) continue;

            try {
                LiveSessionSnapshot encerrada = liveSessionService.encerrarPorAbandono(roomKey);
                if (encerrada != null) sessaoAoVivoService.persistir(encerrada);
            } catch (Exception e) {
                log.error("Falha ao encerrar sessão abandonada {}", roomKey, e);
            }
        }
    }

    private void fechar(WebSocketSession sessao, CloseStatus status) {
        try {
            sessao.close(status);
        } catch (Exception e) {
            log.debug("Falha ao fechar sessão {}: {}", sessao.getId(), e.getMessage());
        }
    }
}
