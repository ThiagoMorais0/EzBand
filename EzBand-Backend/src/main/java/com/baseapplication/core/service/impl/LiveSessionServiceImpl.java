package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.live.AberturaSessao;
import com.baseapplication.core.dto.live.LiveFaixaInfo;
import com.baseapplication.core.dto.live.LiveFaixaNova;
import com.baseapplication.core.dto.live.LiveFaixaTocada;
import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import com.baseapplication.core.dto.live.LiveTimerState;
import com.baseapplication.core.live.LiveComandoInvalidoException;
import com.baseapplication.core.live.LiveSessionStore;
import com.baseapplication.core.live.LiveUsuarioSessao;
import com.baseapplication.core.service.LiveSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

/**
 * Implementação das regras da sessão ao vivo.
 *
 * <p><b>Nenhum método aqui é {@code @Transactional}.</b> A única consulta ao banco acontece
 * uma vez por sessão, na criação, para congelar o repertório no snapshot — depois disso o
 * WebSocket não encosta mais no pool do Hikari. Foi a combinação de conexão longa com sessão
 * do Hibernate viva que derrubou o SSE de notificações; o Modo Performance não repete isso.
 */
@Slf4j
@Service
public class LiveSessionServiceImpl implements LiveSessionService {

    private final LiveSessionStore store;
    private final RepertorioEventoDao repertorioEventoDao;

    /**
     * Um lock por sala serializa o ciclo ler-modificar-gravar do snapshot. Sem ele, dois
     * controladores apertando "próxima" no mesmo instante leriam o mesmo idx e uma das
     * trocas se perderia. Vale enquanto roda uma instância só; com duas, isto vira um lock
     * distribuído no Redis.
     */
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    public LiveSessionServiceImpl(LiveSessionStore store, RepertorioEventoDao repertorioEventoDao) {
        this.store = store;
        this.repertorioEventoDao = repertorioEventoDao;
    }

    // ── API ─────────────────────────────────────────────────────────────

    @Override
    public AberturaSessao abrirOuEntrar(LiveUsuarioSessao usuario) {
        return comLock(usuario.getRoomKey(), () -> {
            LiveSessionSnapshot snapshot = store.buscar(usuario.getRoomKey());
            boolean recemCriada = snapshot == null;
            if (recemCriada) {
                snapshot = new LiveSessionSnapshot(
                        usuario.getIdEvento(),
                        usuario.getTipoEvento(),
                        usuario.getIdBanda(),
                        usuario.getIdUsuario(),
                        carregarFaixas(usuario),
                        System.currentTimeMillis());
                snapshot.setSeq(store.proximoSeq(usuario.getRoomKey()));
                gravar(usuario.getRoomKey(), snapshot);
                log.info("Sessão ao vivo aberta em {} por usuário {} ({} faixas)",
                        usuario.getRoomKey(), usuario.getIdUsuario(), snapshot.getTotalFaixas());
            }
            return new AberturaSessao(snapshot, recemCriada);
        });
    }

    @Override
    public LiveSessionSnapshot buscar(String roomKey) {
        return store.buscar(roomKey);
    }

    @Override
    public LiveSessionSnapshot definirFaixa(LiveUsuarioSessao usuario, Integer idx) {
        return mutar(usuario, snapshot -> {
            exigirControle(snapshot, usuario);
            if (idx == null || idx < 0 || idx >= snapshot.getTotalFaixas()) {
                throw LiveComandoInvalidoException.faixaInvalida();
            }
            return trocarFaixa(snapshot, idx);
        });
    }

    @Override
    public LiveSessionSnapshot moverFaixa(LiveUsuarioSessao usuario, int delta) {
        return mutar(usuario, snapshot -> {
            exigirControle(snapshot, usuario);
            if (snapshot.getTotalFaixas() == 0) {
                throw LiveComandoInvalidoException.faixaInvalida();
            }
            // Do setlist, "próxima" abre a primeira e "anterior" abre a última — o comando
            // nunca é inócuo, e é por isso que NEXT_TRACK não carrega índice do cliente.
            int atual = snapshot.getIdxAtual() == null ? (delta > 0 ? -1 : snapshot.getTotalFaixas()) : snapshot.getIdxAtual();
            int alvo = Math.max(0, Math.min(snapshot.getTotalFaixas() - 1, atual + delta));
            return trocarFaixa(snapshot, alvo);
        });
    }

    @Override
    public LiveSessionSnapshot adicionarFaixa(LiveUsuarioSessao usuario, LiveFaixaNova faixa) {
        if (faixa == null) throw LiveComandoInvalidoException.faixaSemTitulo();
        faixa.normalizar();
        if (!faixa.temTitulo()) throw LiveComandoInvalidoException.faixaSemTitulo();

        return mutar(usuario, snapshot -> {
            exigirControle(snapshot, usuario);

            // Sempre no fim. Inserir no meio deslocaria idxAtual, o log de tocadas e o
            // conjunto de já-tocadas de cada cliente conectado — um bug silencioso que só
            // apareceria no resumo, depois do show, sem ninguém saber de onde veio.
            int idx = snapshot.getTotalFaixas();
            faixa.setIdx(idx);
            snapshot.getFaixas().add(new LiveFaixaInfo(
                    idx, faixa.getTitulo(), faixa.getArtista(), faixa.getDuracaoSegundos()));
            return true;
        });
    }

    @Override
    public LiveSessionSnapshot voltarAoSetlist(LiveUsuarioSessao usuario) {
        return mutar(usuario, snapshot -> {
            exigirControle(snapshot, usuario);
            if (snapshot.getIdxAtual() == null) return false;
            fecharFaixaEmAberto(snapshot, System.currentTimeMillis());
            snapshot.setIdxAtual(null);
            return true;
        });
    }

    @Override
    public LiveSessionSnapshot assumirControle(LiveUsuarioSessao usuario) {
        return mutar(usuario, snapshot -> {
            if (snapshot.isControlador(usuario.getIdUsuario())) return false;
            // Aditivo de propósito: quem já controlava continua controlando. A UI avisa que o
            // ideal é um só; forçar isso no servidor tiraria o controle de alguém no meio de
            // uma música, que é pior do que duas pessoas com o botão.
            snapshot.getControladores().add(usuario.getIdUsuario());
            snapshot.setControleOrfaoDesde(null);
            return true;
        });
    }

    @Override
    public LiveSessionSnapshot liberarControle(LiveUsuarioSessao usuario) {
        return mutar(usuario, snapshot -> {
            if (!snapshot.getControladores().remove(usuario.getIdUsuario())) return false;
            if (snapshot.getControladores().isEmpty()) {
                snapshot.setControleOrfaoDesde(System.currentTimeMillis());
            }
            return true;
        });
    }

    @Override
    public LiveSessionSnapshot reavaliarControle(String roomKey, Set<Long> idsConectados) {
        return comLock(roomKey, () -> {
            LiveSessionSnapshot snapshot = store.buscar(roomKey);
            if (snapshot == null) return null;

            boolean algumControladorOnline = snapshot.getControladores().stream().anyMatch(idsConectados::contains);
            Long antes = snapshot.getControleOrfaoDesde();

            if (algumControladorOnline) {
                if (antes == null) return null;
                snapshot.setControleOrfaoDesde(null);
            } else {
                if (antes != null) return null;
                snapshot.setControleOrfaoDesde(System.currentTimeMillis());
            }

            snapshot.setSeq(store.proximoSeq(roomKey));
            gravar(roomKey, snapshot);
            return snapshot;
        });
    }

    @Override
    public LiveSessionSnapshot iniciarTimer(LiveUsuarioSessao usuario, Integer duracaoSegundos) {
        return mutar(usuario, snapshot -> {
            exigirControle(snapshot, usuario);
            LiveTimerState timer = snapshot.getTimer();
            long agora = System.currentTimeMillis();

            if (timer.getPausadoRestanteMs() != null) {
                // Retomada: o que sobrava vira o novo fim previsto.
                timer.setFimPrevistoEpochMs(agora + timer.getPausadoRestanteMs());
                timer.setPausadoRestanteMs(null);
            } else {
                Integer segundos = duracaoSegundos != null ? duracaoSegundos : timer.getDuracaoSegundos();
                if (segundos == null || segundos <= 0) {
                    throw new LiveComandoInvalidoException("DURACAO_INVALIDA",
                            "Informe a duração do show para iniciar o cronômetro.");
                }
                timer.setDuracaoSegundos(segundos);
                timer.setFimPrevistoEpochMs(agora + segundos * 1000L);
            }
            timer.setAtivo(true);
            return true;
        });
    }

    @Override
    public LiveSessionSnapshot pausarTimer(LiveUsuarioSessao usuario) {
        return mutar(usuario, snapshot -> {
            exigirControle(snapshot, usuario);
            LiveTimerState timer = snapshot.getTimer();
            if (!timer.isAtivo() || timer.getFimPrevistoEpochMs() == null) return false;

            timer.setPausadoRestanteMs(Math.max(0, timer.getFimPrevistoEpochMs() - System.currentTimeMillis()));
            timer.setAtivo(false);
            return true;
        });
    }

    @Override
    public LiveSessionSnapshot zerarTimer(LiveUsuarioSessao usuario) {
        return mutar(usuario, snapshot -> {
            exigirControle(snapshot, usuario);
            LiveTimerState timer = snapshot.getTimer();
            if (!timer.isAtivo() && timer.getFimPrevistoEpochMs() == null && timer.getPausadoRestanteMs() == null) {
                return false;
            }
            timer.setAtivo(false);
            timer.setFimPrevistoEpochMs(null);
            timer.setPausadoRestanteMs(null);
            // A duração configurada fica: zerar é para recomeçar a contagem, não para reconfigurar.
            return true;
        });
    }

    @Override
    public LiveSessionSnapshot encerrar(LiveUsuarioSessao usuario) {
        return comLock(usuario.getRoomKey(), () -> {
            LiveSessionSnapshot snapshot = store.buscar(usuario.getRoomKey());
            if (snapshot == null) throw LiveComandoInvalidoException.semSessao();
            exigirControle(snapshot, usuario);

            fecharFaixaEmAberto(snapshot, System.currentTimeMillis());
            store.remover(usuario.getRoomKey());
            log.info("Sessão ao vivo {} encerrada pelo usuário {}", usuario.getRoomKey(), usuario.getIdUsuario());
            return snapshot;
        });
    }

    @Override
    public LiveSessionSnapshot encerrarPorAbandono(String roomKey) {
        return comLock(roomKey, () -> {
            LiveSessionSnapshot snapshot = store.buscar(roomKey);
            if (snapshot == null) return null;

            fecharFaixaEmAberto(snapshot, System.currentTimeMillis());
            store.remover(roomKey);
            log.info("Sessão ao vivo {} encerrada por abandono", roomKey);
            return snapshot;
        });
    }

    @Override
    public void remover(String roomKey) {
        comLock(roomKey, () -> {
            store.remover(roomKey);
            return null;
        });
        locks.remove(roomKey);
    }

    // ── Internos ────────────────────────────────────────────────────────

    /**
     * Aplica uma mutação sob o lock da sala. A função devolve {@code true} quando algo mudou —
     * só então o seq é incrementado, o snapshot é gravado e o handler transmite.
     */
    private LiveSessionSnapshot mutar(LiveUsuarioSessao usuario, Function<LiveSessionSnapshot, Boolean> mutacao) {
        return comLock(usuario.getRoomKey(), () -> {
            LiveSessionSnapshot snapshot = store.buscar(usuario.getRoomKey());
            if (snapshot == null) throw LiveComandoInvalidoException.semSessao();

            if (!Boolean.TRUE.equals(mutacao.apply(snapshot))) return null;

            snapshot.setSeq(store.proximoSeq(usuario.getRoomKey()));
            gravar(usuario.getRoomKey(), snapshot);
            return snapshot;
        });
    }

    /** Toda gravação passa por aqui para carimbar a última atividade da sala. */
    private void gravar(String roomKey, LiveSessionSnapshot snapshot) {
        snapshot.setAtualizadaEm(System.currentTimeMillis());
        store.salvar(roomKey, snapshot);
    }

    private boolean trocarFaixa(LiveSessionSnapshot snapshot, int alvo) {
        if (snapshot.getIdxAtual() != null && snapshot.getIdxAtual() == alvo) return false;

        long agora = System.currentTimeMillis();
        fecharFaixaEmAberto(snapshot, agora);

        LiveFaixaInfo faixa = snapshot.faixa(alvo);
        snapshot.setIdxAtual(alvo);
        snapshot.getTocadas().add(new LiveFaixaTocada(alvo, faixa != null ? faixa.getTitulo() : null, agora));
        return true;
    }

    private void fecharFaixaEmAberto(LiveSessionSnapshot snapshot, long agora) {
        LiveFaixaTocada aberta = snapshot.faixaEmAberto();
        if (aberta != null) aberta.setAte(agora);
    }

    private void exigirControle(LiveSessionSnapshot snapshot, LiveUsuarioSessao usuario) {
        if (!snapshot.isControlador(usuario.getIdUsuario())) {
            throw LiveComandoInvalidoException.semControle();
        }
    }

    private List<LiveFaixaInfo> carregarFaixas(LiveUsuarioSessao usuario) {
        List<Object[]> linhas = repertorioEventoDao.buscarFaixasParaSessaoAoVivo(
                usuario.getIdEvento(), usuario.getTipoEvento().name());

        List<LiveFaixaInfo> faixas = new ArrayList<>(linhas.size());
        int posicao = 0;
        for (Object[] linha : linhas) {
            // O índice do repertório pode ter buracos depois de remoções, então a posição na
            // lista é o que vale como idx da sessão — é ela que o cliente usa para navegar.
            faixas.add(LiveFaixaInfo.de(
                    posicao++,
                    (String) linha[1],
                    (String) linha[2],
                    (Time) linha[3]));
        }
        return faixas;
    }

    private <T> T comLock(String roomKey, java.util.function.Supplier<T> acao) {
        ReentrantLock lock = locks.computeIfAbsent(roomKey, k -> new ReentrantLock());
        lock.lock();
        try {
            return acao.get();
        } finally {
            lock.unlock();
        }
    }
}
