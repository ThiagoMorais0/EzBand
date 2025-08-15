package com.baseapplication.core.event.listeners;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.event.events.SolicitacaoAgendarEnsaioEvent;
import com.baseapplication.core.event.events.SolicitarEntradaBandaEvent;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.NotificacaoService;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.websocket.controller.WebSocketNotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NotificacaoListener {

    private final NotificacaoService notificacaoService;
    private final WebSocketNotificacaoService websocketService;
    private final BandaService bandaService;
    private final UsuarioService usuarioService;

    @EventListener
    public void handleSolicitarEntradaBanda(SolicitarEntradaBandaEvent event) {
        // 2️⃣ envia em tempo real via WebSocket
        websocketService.enviarNotificacao(event.getBandaId(), TipoParticipante.BANDA,
                "Usuário solicitou entrar na banda");
    }

    @EventListener
    public void handleSolicitacaoAgendarEnsaio(SolicitacaoAgendarEnsaioEvent event) {
        websocketService.enviarNotificacao(event.getEnsaio().getEstudio().getId(), TipoParticipante.ESTUDIO,
                "Banda " + event.getEnsaio().getBanda().getNome() + " está solicitando " +
                        "um agendamento de ensaio no dia " +
                        event.getEnsaio().getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " às " +
                        event.getEnsaio().getHorarioInicio() + ".");
    }
}

