package com.baseapplication.core.event.listeners;

import com.baseapplication.core.controller.NotificacaoController;
import com.baseapplication.core.event.events.*;
import com.baseapplication.core.event.events.resposta.RespostaSolicitacaoAgendarEnsaioEvent;
import com.baseapplication.core.event.events.resposta.RespostaSolicitacaoAgendarShowEvent;
import com.baseapplication.core.model.notificacao.*;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.NotificacaoService;
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
    public void handleSolicitacaoAgendarEnsaio(SolicitacaoAgendarEnsaioEvent event) {
        SolicitacaoAgendarEnsaio notificacao = new SolicitacaoAgendarEnsaio(event.getEnsaio());
        notificacao.setIdEnsaio(event.getEnsaio().getId());
        notificacao.setUrlImagem(event.getEnsaio().getBanda().getUrlLogo());
        notificacao.setTitulo("Novo agendamento!");
        enviar(notificacao);
    }

    @EventListener
    public void handleRespostaSolicitacaoAgendarEnsaio(RespostaSolicitacaoAgendarEnsaioEvent event) {;
        notificacaoService.salvarNotificacao(event.getResposta());
    }

    @EventListener
    public void handleUsuarioExpulsoDeBanda(UsuarioExpulsoDeBandaEvent event) {
        UsuarioExpulsoDeBanda notificacao = new UsuarioExpulsoDeBanda(event.getBanda(), event.getIdUsuario());
        enviar(notificacao);
    }

    @EventListener
    public void handleConviteParaUsuarioIngressarBanda(ConviteParaUsuarioIngressarBandaEvent event) {
        ConviteParaUsuarioIngressarBanda notificacao = new ConviteParaUsuarioIngressarBanda(
                event.getIdUsuarioConvidado(),
                event.getBanda()
        );
        enviar(notificacao);
    }

    @EventListener
    public void handleSolicitacaoIngressarBanda(SolicitacaoIngressarBandaEvent event) {
        SolicitacaoParaIngressarBanda notificacao = new SolicitacaoParaIngressarBanda(
                event.getUsuario(),
                event.getBanda(),
                event.getInstrumento()
        );
        enviar(notificacao);
    }

    @EventListener
    public void handleEnviarConviteParaMusicoEvento(ConviteParaMusicoEventoEvent event) {
        ConviteParaEvento notificacao = new ConviteParaEvento(
                event.getEvento(),
                event.getIdUsuarioConvidado(),
                event.getCache(),
                event.getInstrumentos()
        );
        enviar(notificacao);
    }

    @EventListener
    public void handleSolicitacaoAgendarShow(SolicitacaoAgendarShowEvent event) {
        SolicitacaoAgendarShow notificacao = new SolicitacaoAgendarShow(event.getShow());
        notificacao.setIdShow(event.getShow().getId());
        notificacao.setUrlImagem(event.getShow().getBanda().getUrlLogo());
        notificacao.setTitulo("Novo agendamento!");
        enviar(notificacao);
    }

    @EventListener
    public void handleRespostaSolicitacaoAgendarShow(RespostaSolicitacaoAgendarShowEvent event) {
        notificacaoService.salvarNotificacao(event.getResposta());
    }

    @EventListener
    public void handleNovoSeguidor(NovoSeguidorEvent event) {
        NovoSeguidor notificacao = new NovoSeguidor(event.getRemetente(), event.getIdDestinatario());
        enviar(notificacao);
    }

}

