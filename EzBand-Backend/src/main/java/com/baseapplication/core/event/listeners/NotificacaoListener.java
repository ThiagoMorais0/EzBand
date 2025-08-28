package com.baseapplication.core.event.listeners;

import com.baseapplication.core.controller.NotificacaoController;
import com.baseapplication.core.event.events.ConviteParaUsuarioIngressarBandaEvent;
import com.baseapplication.core.event.events.SolicitacaoAgendarEnsaioEvent;
import com.baseapplication.core.event.events.SolicitacaoIngressarBandaEvent;
import com.baseapplication.core.event.events.UsuarioExpulsoDeBandaEvent;
import com.baseapplication.core.event.events.resposta.RespostaSolicitacaoAgendarEnsaioEvent;
import com.baseapplication.core.model.notificacao.ConviteParaUsuarioIngressarBanda;
import com.baseapplication.core.model.notificacao.SolicitacaoAgendarEnsaio;
import com.baseapplication.core.model.notificacao.SolicitacaoParaIngressarBanda;
import com.baseapplication.core.model.notificacao.UsuarioExpulsoDeBanda;
import com.baseapplication.core.model.superClasses.Notificacao;
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

    private void enviar(Notificacao notificacao) {
        notificacaoService.salvarNotificacao(notificacao);
        notificacaoController.enviarNotificacao(notificacao);
    }

    @EventListener
    @Transactional
    public void handleSolicitacaoAgendarEnsaio(SolicitacaoAgendarEnsaioEvent event) {
        SolicitacaoAgendarEnsaio notificacao = new SolicitacaoAgendarEnsaio(event.getEnsaio());
        notificacao.setIdEnsaio(event.getEnsaio().getId());
        notificacao.setUrlImagem(event.getEnsaio().getBanda().getUrlLogo());
        notificacao.setTitulo("Novo agendamento!");
        enviar(notificacao);
    }

    @EventListener
    @Transactional
    public void handleRespostaSolicitacaoAgendarEnsaio(RespostaSolicitacaoAgendarEnsaioEvent event) {;
        notificacaoService.salvarNotificacao(event.getResposta());
    }

    @EventListener
    @Transactional
    public void handleUsuarioExpulsoDeBanda(UsuarioExpulsoDeBandaEvent event) {
        UsuarioExpulsoDeBanda notificacao = new UsuarioExpulsoDeBanda(event.getIdBanda(), event.getIdUsuario());
        enviar(notificacao);
    }

    @EventListener
    @Transactional
    public void handleConviteParaUsuarioIngressarBanda(ConviteParaUsuarioIngressarBandaEvent event) {
        ConviteParaUsuarioIngressarBanda notificacao = new ConviteParaUsuarioIngressarBanda(
                event.getIdBanda(),
                event.getIdUsuarioConvidado()
        );
        enviar(notificacao);
    }

    @EventListener
    @Transactional
    public void handleSolicitacaoIngressarBanda(SolicitacaoIngressarBandaEvent event) {
        SolicitacaoParaIngressarBanda notificacao = new SolicitacaoParaIngressarBanda(
                event.getUsuario(),
                event.getIdBanda(),
                event.getInstrumento()
        );
        enviar(notificacao);
    }


}

