package com.baseapplication.core.dto;

import com.baseapplication.core.model.superClasses.Notificacao;

public record NotificacaoDTO(String mensagem, Long destinatarioId, String name) {
}
