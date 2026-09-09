package com.baseapplication.core.controller;

import com.baseapplication.core.dao.MusicoEventoDao;
import com.baseapplication.core.dto.live.EdicaoResumoDTO;
import com.baseapplication.core.dto.live.LiveSessionStatusDTO;
import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import com.baseapplication.core.dto.live.ResumoSessaoDTO;
import com.baseapplication.core.enums.SituacaoMusicoEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.live.LiveSessionRegistry;
import com.baseapplication.core.live.LiveUsuarioSessao;
import com.baseapplication.core.service.LiveSessionService;
import com.baseapplication.core.service.SessaoAoVivoService;
import com.baseapplication.core.utils.Context;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints REST do Modo Performance. O tempo real fica no WebSocket
 * ({@code /ws/live/{tipoEvento}/{idEvento}}); aqui só o que precisa ser consultado antes de
 * abrir o socket.
 */
@RestController
@RequestMapping("/live")
public class LiveSessionController {

    private final LiveSessionService liveSessionService;
    private final SessaoAoVivoService sessaoAoVivoService;
    private final LiveSessionRegistry registry;
    private final MusicoEventoDao musicoEventoDao;

    public LiveSessionController(LiveSessionService liveSessionService,
                                 SessaoAoVivoService sessaoAoVivoService,
                                 LiveSessionRegistry registry,
                                 MusicoEventoDao musicoEventoDao) {
        this.liveSessionService = liveSessionService;
        this.sessaoAoVivoService = sessaoAoVivoService;
        this.registry = registry;
        this.musicoEventoDao = musicoEventoDao;
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento) {
        Long idUsuario = Context.getUsuarioLogado().getId();
        if (idUsuario == null || !participaDoEvento(idEvento, tipoEvento, idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String roomKey = LiveUsuarioSessao.roomKey(tipoEvento, idEvento);
        LiveSessionSnapshot snapshot = liveSessionService.buscar(roomKey);
        if (snapshot == null) {
            return ResponseEntity.ok(LiveSessionStatusDTO.inativa());
        }

        return ResponseEntity.ok(new LiveSessionStatusDTO(
                true,
                snapshot.getIniciadaEm(),
                snapshot.getIniciadaPor(),
                snapshot.getIdxAtual(),
                registry.presenca(roomKey, snapshot.getControladores())));
    }

    /** Resumo da última sessão encerrada deste evento. 204 quando a banda ainda não subiu ao palco. */
    @GetMapping("/resumo")
    public ResponseEntity<?> resumo(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento) {
        Long idUsuario = Context.getUsuarioLogado().getId();
        if (idUsuario == null || !participaDoEvento(idEvento, tipoEvento, idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ResumoSessaoDTO resumo = sessaoAoVivoService.buscarUltimoDoEvento(idEvento, tipoEvento, idUsuario);
        return resumo == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(resumo);
    }

    /** Todas as sessões já gravadas do evento, da mais recente para a mais antiga. */
    @GetMapping("/resumos")
    public ResponseEntity<?> resumos(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento) {
        Long idUsuario = Context.getUsuarioLogado().getId();
        if (idUsuario == null || !participaDoEvento(idEvento, tipoEvento, idUsuario)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(sessaoAoVivoService.buscarDoEvento(idEvento, tipoEvento, idUsuario));
    }

    /**
     * Corrige o resumo de uma sessão já encerrada.
     *
     * <p>Existe porque o registro automático não sabe distinguir um clique errado de uma
     * música tocada duas vezes — só quem estava no palco sabe. A permissão é checada no
     * serviço, contra a banda dona da sessão.
     */
    @PutMapping("/resumo/{idSessao}")
    public ResponseEntity<?> editarResumo(@PathVariable Long idSessao, @RequestBody EdicaoResumoDTO edicao) {
        Long idUsuario = Context.getUsuarioLogado().getId();
        if (idUsuario == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(sessaoAoVivoService.editarResumo(idSessao, edicao, idUsuario));
    }

    private boolean participaDoEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuario) {
        return musicoEventoDao.findByIdIdEventoAndIdTipoEvento(idEvento, tipoEvento).stream()
                .anyMatch(me -> me.getId() != null
                        && idUsuario.equals(me.getId().getIdUsuario())
                        && me.getSituacao() == SituacaoMusicoEvento.ATIVO);
    }
}
