package com.baseapplication.core.controller;

import com.baseapplication.core.model.notificacao.AprovacaoEvento;
import com.baseapplication.core.model.superClasses.Notificacao;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/notificacoes")
public class NotificacaoController {


    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Notificacao> streamNotificacoes() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> new AprovacaoEvento());
    }

}
