package com.baseapplication.core.controller;

import com.baseapplication.core.dto.NotificacaoDTO;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.RespostaNotificacaoDTO;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.NotificacaoService;
import com.baseapplication.core.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {
    private final NotificacaoService notificacaoService;

    private String key(Long id, TipoParticipante tipo) {
        return id + ":" + tipo.name();
    }

    @GetMapping(value = "/{destinatarioId}/{destinatarioTipo}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificacaoDTO> streamNotificacoes(@PathVariable Long destinatarioId,
                                                   @PathVariable TipoParticipante destinatarioTipo) {

        return notificacaoService.streamNotificacoes(destinatarioId, destinatarioTipo);
    }



    public void enviarNotificacao(Notificacao notificacao) {
       notificacaoService.enviarNotificacaoSink(notificacao);
    }

    @GetMapping("/deletarTodas")
    public void deletarTodas(){
        notificacaoService.deletarTodos();
    }

    @PostMapping("/responder/{id}")
    public ResponseEntity<Void> responder(
            @PathVariable Long id,
            @RequestBody RespostaNotificacaoDTO respostaDTO) {

        notificacaoService.responderNotificacao(id, respostaDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lerNotificacao")
    public ResponseEntity<?> lerNotificacao(@RequestBody Long id){
        try{
            notificacaoService.lerNotificacao(id);
            return ResponseEntity.ok().build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
