package com.baseapplication.core.controller;

import com.baseapplication.core.dto.NotificacaoDTO;
import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/notificacoes")
@RequiredArgsConstructor
public class NotificacaoController {

    private final Map<String, Sinks.Many<NotificacaoDTO>> sinks = new ConcurrentHashMap<>();
    private final NotificacaoService notificacaoService;

    private String key(Long id, TipoParticipante tipo) {
        return id + ":" + tipo.name();
    }

    @GetMapping(value = "/{destinatarioId}/{destinatarioTipo}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<NotificacaoDTO> streamNotificacoes(@PathVariable Long destinatarioId,
                                                @PathVariable TipoParticipante destinatarioTipo) {
        String chave = key(destinatarioId, destinatarioTipo);

        Sinks.Many<NotificacaoDTO> sink = sinks.computeIfAbsent(
                chave,
                k -> Sinks.many().multicast().onBackpressureBuffer()
        );

        Flux<NotificacaoDTO> naoLidas = Flux.fromIterable(notificacaoService
                        .buscarNaoLidas(destinatarioId, destinatarioTipo).stream()
                        .map(i -> new NotificacaoDTO(
                                i.getMensagem(),
                                i.getDestinatarioId(),
                                i.getDestinatarioTipo().name(),
                                i.getRemetenteTipo().name()))
                        .toList());

        // Concatena as não lidas primeiro, e depois segue em tempo real
        return Flux.concat(
                naoLidas,
                sink.asFlux().doFinally(signal -> sinks.remove(chave))
        );
    }


    public void enviarNotificacao(Notificacao notificacao) {
        String chave = key(notificacao.getDestinatarioId(), notificacao.getDestinatarioTipo());
        Sinks.Many<NotificacaoDTO> sink = sinks.get(chave);
        System.out.println("Tentando enviar notificação para " + chave + ": " + notificacao.getMensagem());
        if (sink != null) {
            System.out.println("Enviando notificação para " + chave + ": " + notificacao.getMensagem());
            sink.tryEmitNext(new NotificacaoDTO(
                    notificacao.getMensagem(),
                    notificacao.getDestinatarioId(),
                    notificacao.getDestinatarioTipo().name(),
                    notificacao.getRemetenteTipo().name()
            ));

        }
    }

    @GetMapping("/deletarTodas")
    public void deletarTodas(){
        notificacaoService.deletarTodos();
    }
}
