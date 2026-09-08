package com.baseapplication.core.config;

import com.baseapplication.core.dao.MusicoEventoDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.enums.SituacaoMusicoEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.live.LiveUsuarioSessao;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.EventoService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

/**
 * Autentica e autoriza o handshake do Modo Performance.
 *
 * <p>Caminho: {@code /ws/live/{tipoEvento}/{idEvento}} — atrás do nginx o cliente chama
 * {@code /api/ws/live/...}, que já repassa os cabeçalhos de Upgrade.
 *
 * <p>Sendo mesma origem, o cookie {@code jwt} httpOnly viaja sozinho no handshake. Nada de
 * token na query string como no ChatService: query string vaza em log de proxy e em
 * histórico de navegador.
 */
@Slf4j
@Component
public class LiveSessionHandshakeInterceptor implements HandshakeInterceptor {

    private final TokenService tokenService;
    private final UsuarioDao usuarioDao;
    private final MusicoEventoDao musicoEventoDao;
    private final EventoService eventoService;

    public LiveSessionHandshakeInterceptor(TokenService tokenService,
                                           UsuarioDao usuarioDao,
                                           MusicoEventoDao musicoEventoDao,
                                           EventoService eventoService) {
        this.tokenService = tokenService;
        this.usuarioDao = usuarioDao;
        this.musicoEventoDao = musicoEventoDao;
        this.eventoService = eventoService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return recusar(response, HttpStatus.BAD_REQUEST, "Requisição não-servlet");
        }

        String[] partes = request.getURI().getPath().split("/");
        if (partes.length < 2) {
            return recusar(response, HttpStatus.BAD_REQUEST, "Caminho inválido");
        }

        TipoEvento tipoEvento;
        Long idEvento;
        try {
            tipoEvento = TipoEvento.valueOf(partes[partes.length - 2].toUpperCase());
            idEvento = Long.valueOf(partes[partes.length - 1]);
        } catch (Exception e) {
            return recusar(response, HttpStatus.BAD_REQUEST, "Evento inválido no caminho");
        }

        String token = recuperarToken(servletRequest);
        if (token == null) {
            return recusar(response, HttpStatus.UNAUTHORIZED, "Sem credencial");
        }

        String email = tokenService.validarToken(token);
        if (email == null || email.isEmpty()) {
            return recusar(response, HttpStatus.UNAUTHORIZED, "Credencial inválida");
        }

        Usuario usuario = usuarioDao.findByEmail(email);
        if (usuario == null) {
            return recusar(response, HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        }

        MusicoEvento participacao = buscarParticipacao(idEvento, tipoEvento, usuario.getId());
        if (participacao == null) {
            log.warn("Usuário {} tentou entrar na sessão ao vivo de {}:{} sem participar do evento",
                    usuario.getId(), tipoEvento, idEvento);
            return recusar(response, HttpStatus.FORBIDDEN, "Você não faz parte deste evento");
        }

        Long idBanda = resolverIdBanda(idEvento, tipoEvento);
        if (idBanda == null) {
            return recusar(response, HttpStatus.NOT_FOUND, "Evento não encontrado");
        }

        LiveUsuarioSessao sessao = new LiveUsuarioSessao();
        sessao.setIdUsuario(usuario.getId());
        sessao.setNome(usuario.getNome());
        sessao.setUrlFotoPerfil(usuario.getUrlFotoPerfil());
        sessao.setInstrumentos(participacao.getInstrumentos());
        sessao.setIdEvento(idEvento);
        sessao.setTipoEvento(tipoEvento);
        sessao.setIdBanda(idBanda);
        sessao.setRoomKey(LiveUsuarioSessao.roomKey(tipoEvento, idEvento));
        sessao.setConectadoEm(System.currentTimeMillis());
        sessao.setNotificarMembros(lerNotificarMembros(request.getURI().getQuery()));

        attributes.put(LiveUsuarioSessao.ATTR, sessao);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // nada a fazer
    }

    /**
     * {@code ?notificar=0} — quem abriu o palco respondeu que NÃO quer avisar a banda.
     *
     * <p>Só um booleano, nada sensível: ao contrário do token, pode viajar na query string.
     * Ausente ou ilegível vale verdadeiro, que é o comportamento de sempre.
     */
    private boolean lerNotificarMembros(String query) {
        if (query == null || query.isBlank()) return true;
        for (String par : query.split("&")) {
            int igual = par.indexOf('=');
            if (igual < 0) continue;
            if (!"notificar".equals(par.substring(0, igual))) continue;
            String valor = par.substring(igual + 1);
            return !"0".equals(valor) && !"false".equalsIgnoreCase(valor);
        }
        return true;
    }

    private MusicoEvento buscarParticipacao(Long idEvento, TipoEvento tipoEvento, Long idUsuario) {
        List<MusicoEvento> participantes = musicoEventoDao.findByIdIdEventoAndIdTipoEvento(idEvento, tipoEvento);
        return participantes.stream()
                .filter(me -> me.getId() != null && idUsuario.equals(me.getId().getIdUsuario()))
                // Quem recusou ou saiu do evento não entra no palco. Convite pendente também
                // não: a sessão ao vivo é de quem está tocando, não de quem foi chamado.
                .filter(me -> me.getSituacao() == SituacaoMusicoEvento.ATIVO)
                .findFirst()
                .orElse(null);
    }

    private Long resolverIdBanda(Long idEvento, TipoEvento tipoEvento) {
        try {
            Evento evento = eventoService.buscarPorId(idEvento, tipoEvento);
            return evento != null && evento.getBanda() != null ? evento.getBanda().getId() : null;
        } catch (Exception e) {
            log.warn("Falha ao resolver banda do evento {}:{} — {}", tipoEvento, idEvento, e.getMessage());
            return null;
        }
    }

    private String recuperarToken(ServletServerHttpRequest request) {
        Cookie[] cookies = request.getServletRequest().getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        String header = request.getServletRequest().getHeader("Authorization");
        return header == null ? null : header.replace("Bearer ", "");
    }

    private boolean recusar(ServerHttpResponse response, HttpStatus status, String motivo) {
        response.setStatusCode(status);
        log.debug("Handshake da sessão ao vivo recusado ({}): {}", status, motivo);
        return false;
    }
}
