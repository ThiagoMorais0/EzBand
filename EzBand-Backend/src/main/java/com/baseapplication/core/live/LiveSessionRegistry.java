package com.baseapplication.core.live;

import com.baseapplication.core.dto.live.LiveMembroPresenca;
import com.baseapplication.core.dto.live.LiveServerMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Quem está conectado em cada sala, e o fanout das mensagens.
 *
 * <p>Hoje o fanout é local porque roda uma instância só ({@code api} no compose). Se um dia
 * forem duas, o que muda é esta classe passar a publicar num canal Redis e assinar o mesmo —
 * o handler e o service não sabem que ela existe além de {@code broadcast}.
 *
 * <p>As sessões são indexadas por id porque chegam aqui embrulhadas em
 * {@code ConcurrentWebSocketSessionDecorator}: na desconexão o container devolve a sessão
 * crua, que não é o mesmo objeto que foi guardado.
 */
@Slf4j
@Component
public class LiveSessionRegistry {

    private final Map<String, Map<String, WebSocketSession>> salas = new ConcurrentHashMap<>();
    private final ObjectMapper mapper;

    public LiveSessionRegistry(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public void entrar(String roomKey, WebSocketSession sessao) {
        salas.computeIfAbsent(roomKey, k -> new ConcurrentHashMap<>()).put(sessao.getId(), sessao);
    }

    /** Remove a sessão e devolve true se a sala ficou sem ninguém. */
    public boolean sair(String roomKey, String sessionId) {
        Map<String, WebSocketSession> sessoes = salas.get(roomKey);
        if (sessoes == null) return true;
        sessoes.remove(sessionId);
        if (sessoes.isEmpty()) {
            salas.remove(roomKey);
            return true;
        }
        return false;
    }

    public Collection<WebSocketSession> sessoes(String roomKey) {
        Map<String, WebSocketSession> sessoes = salas.get(roomKey);
        return sessoes == null ? Collections.emptyList() : sessoes.values();
    }

    public Set<String> salasComConexao() {
        return new HashSet<>(salas.keySet());
    }

    public Set<Long> idsUsuariosConectados(String roomKey) {
        return sessoes(roomKey).stream()
                .map(this::usuarioDe)
                .filter(java.util.Objects::nonNull)
                .map(LiveUsuarioSessao::getIdUsuario)
                .collect(Collectors.toSet());
    }

    /**
     * Presença da sala, ordenada por quem chegou primeiro.
     *
     * <p>A fonte é a lista de sockets abertos, não o snapshot: quem caiu não está olhando a
     * tela, mesmo que o estado da sessão continue vivo no store. Um mesmo músico com duas
     * abas abertas aparece uma vez só, contando desde a mais antiga.
     */
    public List<LiveMembroPresenca> presenca(String roomKey, List<Long> controladores) {
        Map<Long, LiveMembroPresenca> porUsuario = new LinkedHashMap<>();
        for (WebSocketSession sessao : sessoes(roomKey)) {
            LiveUsuarioSessao usuario = usuarioDe(sessao);
            if (usuario == null) continue;
            porUsuario.merge(usuario.getIdUsuario(), usuario.toPresenca(),
                    (antigo, novo) -> antigo.getDesde() <= novo.getDesde() ? antigo : novo);
        }
        List<LiveMembroPresenca> lista = new ArrayList<>(porUsuario.values());
        lista.forEach(m -> m.setControlador(controladores != null && controladores.contains(m.getIdUsuario())));
        lista.sort(Comparator.comparing(LiveMembroPresenca::getDesde));
        return lista;
    }

    public LiveUsuarioSessao usuarioDe(WebSocketSession sessao) {
        return (LiveUsuarioSessao) sessao.getAttributes().get(LiveUsuarioSessao.ATTR);
    }

    public void broadcast(String roomKey, LiveServerMessage mensagem) {
        String payload = serializar(mensagem);
        if (payload == null) return;
        for (WebSocketSession sessao : sessoes(roomKey)) {
            enviarPayload(sessao, payload);
        }
    }

    public void enviar(WebSocketSession sessao, LiveServerMessage mensagem) {
        String payload = serializar(mensagem);
        if (payload != null) enviarPayload(sessao, payload);
    }

    /** Ping de protocolo: o browser responde pong sozinho, sem passar pela aplicação. */
    public void ping(String roomKey) {
        for (WebSocketSession sessao : sessoes(roomKey)) {
            if (!sessao.isOpen()) continue;
            try {
                sessao.sendMessage(new PingMessage());
            } catch (IOException e) {
                log.debug("Ping falhou na sessão {}: {}", sessao.getId(), e.getMessage());
            }
        }
    }

    private String serializar(LiveServerMessage mensagem) {
        try {
            return mapper.writeValueAsString(mensagem);
        } catch (Exception e) {
            log.error("Falha ao serializar mensagem {} da sessão ao vivo", mensagem.getTipo(), e);
            return null;
        }
    }

    private void enviarPayload(WebSocketSession sessao, String payload) {
        if (!sessao.isOpen()) return;
        try {
            // Seguro porque o handler embrulha tudo em ConcurrentWebSocketSessionDecorator:
            // sendMessage não é thread-safe e o broadcast vem de threads diferentes (comandos
            // de vários controladores, scheduler de ping, desconexões).
            sessao.sendMessage(new TextMessage(payload));
        } catch (IOException e) {
            log.debug("Falha ao enviar para sessão {}: {}", sessao.getId(), e.getMessage());
        }
    }
}
