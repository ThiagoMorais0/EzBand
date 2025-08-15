package com.baseapplication.core.websocket.controller;

import com.baseapplication.core.enums.TipoParticipante;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificacaoService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificacaoService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void enviarNotificacao(Long destinatarioId, TipoParticipante tipo, String mensagem) {
        String destino = "/topic/notificacoes-" + tipo + "-" + destinatarioId;
        messagingTemplate.convertAndSend(destino, mensagem);
    }
}
