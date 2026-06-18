package com.baseapplication.core.event.listeners;

import com.baseapplication.core.controller.NotificacaoController;
import com.baseapplication.core.dao.ConfiguracaoNotificacaoUsuarioDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.event.events.*;
import com.baseapplication.core.event.events.resposta.RespostaSolicitacaoAgendarEnsaioEvent;
import com.baseapplication.core.event.events.resposta.RespostaSolicitacaoAgendarShowEvent;
import com.baseapplication.core.model.ConfiguracaoNotificacaoUsuario;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.notificacao.*;
import com.baseapplication.core.event.events.NovoEventoMarcadoEvent;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.NotificacaoService;
import com.baseapplication.core.service.WebPushService;
import com.baseapplication.core.service.WhatsappService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificacaoListener {

    private final NotificacaoController notificacaoController;
    private final NotificacaoService notificacaoService;
    private final WhatsappService whatsappService;
    private final WebPushService webPushService;
    private final ConfiguracaoNotificacaoUsuarioDao configuracaoNotificacaoDao;
    private final UsuarioDao usuarioDao;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    private void enviar(Notificacao notificacao) {
        boolean salva = notificacaoService.salvarNotificacao(notificacao);
        if (salva) {
            notificacaoController.enviarNotificacao(notificacao);
            dispararWhatsappSeNecessario(notificacao);
            webPushService.enviar(notificacao);
        }
    }

    private void dispararWhatsappSeNecessario(Notificacao notificacao) {
        String tipoNotif = notificacao.getTipoNotificacao();
        Long idDestinatario = notificacao.getDestinatarioId();

        if (!TipoParticipante.USUARIO.equals(notificacao.getDestinatarioTipo())) {
            log.debug("[WhatsApp] Notificação {} ignorada: destinatário não é USUARIO (tipo={})", tipoNotif, notificacao.getDestinatarioTipo());
            return;
        }

        Long idUsuario = notificacao.getDestinatarioId();
        Optional<ConfiguracaoNotificacaoUsuario> configOpt = configuracaoNotificacaoDao.findByIdUsuario(idUsuario);
        if (configOpt.isEmpty()) {
            log.info("[WhatsApp] Notificação {} NÃO enviada: usuário {} não tem ConfiguracaoNotificacaoUsuario.", tipoNotif, idUsuario);
            return;
        }
        if (!Boolean.TRUE.equals(configOpt.get().getReceberNotificacoesWhatsapp())) {
            log.info("[WhatsApp] Notificação {} NÃO enviada: usuário {} tem receberNotificacoesWhatsapp=false.", tipoNotif, idUsuario);
            return;
        }

        Optional<Usuario> usuarioOpt = usuarioDao.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            log.warn("[WhatsApp] Notificação {} NÃO enviada: usuário {} não encontrado.", tipoNotif, idUsuario);
            return;
        }
        Usuario usuario = usuarioOpt.get();

        if (!Boolean.TRUE.equals(usuario.getCelularValidado())) {
            log.info("[WhatsApp] Notificação {} NÃO enviada: celular do usuário {} não está validado.", tipoNotif, idUsuario);
            return;
        }
        if (usuario.getCelular() == null) {
            log.info("[WhatsApp] Notificação {} NÃO enviada: usuário {} não tem celular cadastrado.", tipoNotif, idUsuario);
            return;
        }

        String titulo = notificacao.getTitulo() != null ? notificacao.getTitulo() : "EzBand";
        String mensagem = "*" + titulo + "*\n" + notificacao.getMensagem();

        if (notificacao instanceof ConviteParaUsuarioIngressarBanda convite && convite.getLinkToken() != null) {
            mensagem += "\n\nClique no link para aceitar o convite:\n" + frontendUrl + "/aceitar-convite?token=" + convite.getLinkToken();
        }

        log.info("[WhatsApp] Enviando notificação {} para usuário {} ({})", tipoNotif, idUsuario, usuario.getCelular());
        whatsappService.enviarMensagem(usuario.getCelular(), mensagem);
        log.info("[WhatsApp] Notificação {} enviada com sucesso para usuário {}", tipoNotif, idUsuario);
    }

    @EventListener
    public void handleSolicitacaoAgendarEnsaio(SolicitacaoAgendarEnsaioEvent event) {
        Ensaio ensaio = event.getEnsaio();
        List<Long> destinatarioIds = event.getDestinatarioIds();
        log.info("Processando SolicitacaoAgendarEnsaio: ensaioId={} destinatarios={}", ensaio.getId(), destinatarioIds);
        for (Long destinatarioId : destinatarioIds) {
            SolicitacaoAgendarEnsaio notificacao = new SolicitacaoAgendarEnsaio(ensaio, destinatarioId);
            notificacao.setIdEnsaio(ensaio.getId());
            notificacao.setUrlImagem(ensaio.getBanda().getUrlLogo());
            notificacao.setTitulo("Novo agendamento!");
            log.info("Enviando notificação para destinatarioId={}", destinatarioId);
            enviar(notificacao);
        }
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

    @EventListener
    public void handleEnsaioAprovadoPeloEstudio(EnsaioAprovadoPeloEstudioEvent event) {
        EnsaioAprovadoPeloEstudio notificacao = new EnsaioAprovadoPeloEstudio(event.getEnsaio(), event.getEstudio());
        enviar(notificacao);
    }

    @EventListener
    public void handleEnsaioRecusadoPeloEstudio(EnsaioRecusadoPeloEstudioEvent event) {
        EnsaioRecusadoPeloEstudio notificacao = new EnsaioRecusadoPeloEstudio(event.getEnsaio(), event.getEstudio(), event.getMotivo());
        enviar(notificacao);
    }

    @EventListener
    public void handleEnsaioCanceladoPeloEstudio(EnsaioCanceladoPeloEstudioEvent event) {
        EnsaioCanceladoPeloEstudio notificacao = new EnsaioCanceladoPeloEstudio(event.getEnsaio(), event.getEstudio(), event.getMotivo());
        enviar(notificacao);
    }

    @EventListener
    public void handleNovoEventoMarcado(NovoEventoMarcadoEvent event) {
        for (Long destinatarioId : event.getDestinatarioIds()) {
            NovoEventoMarcado notificacao = new NovoEventoMarcado(
                    event.getIdEvento(),
                    event.getTipoEvento(),
                    event.getIdBanda(),
                    event.getNomeBanda(),
                    event.getUrlLogoBanda(),
                    event.getLocal(),
                    event.getIdCriador(),
                    event.getNomeCriador(),
                    destinatarioId
            );
            enviar(notificacao);
        }
    }

}

