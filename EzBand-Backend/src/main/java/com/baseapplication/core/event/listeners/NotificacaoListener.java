package com.baseapplication.core.event.listeners;

import com.baseapplication.core.controller.NotificacaoController;
import com.baseapplication.core.event.events.SolicitacaoAgendarEnsaioEvent;
import com.baseapplication.core.model.notificacao.SolicitacaoAgendarEnsaio;
import com.baseapplication.core.service.NotificacaoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacaoListener {

    private final NotificacaoController notificacaoController;
    private final NotificacaoService notificacaoService;

//    @EventListener
//    public void handleSolicitarEntradaBanda(SolicitarEntradaBandaEvent event) {
//        notificacaoController.enviarNotificacao(
//                new SolicitarEntradaBandaEvent("Usuário solicitou entrar na banda " + event.getBandaId())
//        );
//    }

    @EventListener
    @Transactional
    public void handleSolicitacaoAgendarEnsaio(SolicitacaoAgendarEnsaioEvent event) {
        SolicitacaoAgendarEnsaio notificacao = new SolicitacaoAgendarEnsaio(event.getEnsaio());
        notificacaoService.salvarNotificacao(notificacao);
        notificacaoController.enviarNotificacao(notificacao);
    }
}

