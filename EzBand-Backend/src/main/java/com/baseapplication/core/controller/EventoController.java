package com.baseapplication.core.controller;

import java.util.List;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.EventoService;
import com.baseapplication.core.utils.DateUtils;

@RestController
@RequestMapping("/evento")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/buscarPorId")
    public ResponseEntity<?> buscar(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento) {
        try {
            return ResponseEntity.ok(new EventoDTO(eventoService.buscarPorId(idEvento, tipoEvento)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarInformacoesEvento")
    public InformacoesEventoDTO buscarInformacoesEvento(@RequestParam Long idEvento,
                                                        @RequestParam TipoEvento tipoEvento) {
        return eventoService.buscarPobuscarInformacoesEventorId(idEvento, tipoEvento);

    }

    @PostMapping("/atualizarInformacoesEvento")
    public void atualizarInformacoesEvento(@RequestBody InformacoesEventoDTO informacoesEventoDTO) {
        eventoService.atualizarInformacoesEvento(informacoesEventoDTO);

    }

    @GetMapping("/buscarMusicoParaEvento")
    public MusicoEventoDTO buscarMusicoParaEvento(@RequestParam String contato, @RequestParam TipoContato tipoContato) {
        return eventoService.buscarMusicoParaEvento(contato, tipoContato);

    }

    @PostMapping("/enviarConviteParaEvento")
    public void enviarConviteParaEvento(@RequestBody ConviteEventoDTO conviteEvento) {
        eventoService.enviarConviteParaEvento(conviteEvento);

    }

    @GetMapping("/buscarRepertorioEvento")
    public ResponseEntity<?> buscarRepertorioEvento(@RequestParam Long idEvento,
                                                    @RequestParam String tipoEvento) {
        try {
            return ResponseEntity.ok(eventoService.buscarRepertorioEvento(idEvento, TipoEvento.valueOf(tipoEvento)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/atualizarRepertorioEvento")
    public ResponseEntity<?> atualizarRepertorioEvento(@RequestBody AtualizacaoRepertorioEventoDTO atualizacaoRepertorio) {
        try {
            eventoService.atualizarRepertorioEvento(atualizacaoRepertorio);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/atualizarMusicaRepertorio")
    public ResponseEntity<?> atualizarMusicaRepertorio(@RequestBody AtualizacaoMusicaRepertorioDTO atualizacaoMusicaRepertorio) {
        try {
            eventoService.atualizarMusicaRepertorio(atualizacaoMusicaRepertorio);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarMembrosEDisponibilidadeParaShow")
    public ResponseEntity<?> buscarMembrosParaShow(@RequestParam Long idBanda,
                                                   @RequestParam String data) {
        return eventoService.buscarMembrosEDisponibilidadeParaShow(idBanda, DateUtils.stringToLocalDate(data));
    }

    @PostMapping("/marcarShow")
    public ResponseEntity<?> marcarShow(@RequestBody NovoShowDTO novoShowDTO) {
        try {
            eventoService.marcarShow(novoShowDTO);
            return ResponseEntity.ok("Show marcado com sucseso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/marcarEnsaio")
    public ResponseEntity<?> marcarEnsaio(@RequestBody NovoEnsaioDTO novoEnsaioDTO) {
        try {
            eventoService.marcarEnsaio(novoEnsaioDTO);
            return ResponseEntity.ok("Ensaio marcado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/isNotificacaoShowAceitaPorTodosMembros")
    public boolean isNotificacaoShowAceitaPorTodosMembros(Long idShow) {
        return eventoService.isNotificacaoShowAceitaPorTodosMembros(idShow);
    }

    @PostMapping("/aceitarNotificacao")
    public void aceitarNotificacao(@RequestParam Long idNotificacao) {
        eventoService.aceitarNotificacao(idNotificacao);
    }

    @PostMapping("/recusarNotificacao")
    public void recusarNotificacao(@RequestParam Long idNotificacao) {
        eventoService.recusarNotificacao(idNotificacao);
    }

    @PostMapping("/cancelarEvento")
    public ResponseEntity<?> cancelarEvento(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento) {
        try {
            eventoService.cancelarEvento(idEvento, tipoEvento);
            return ResponseEntity.ok("Evento cancelado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
