package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.superClasses.Notificacao;

public record NotificacaoDTO(
        Long id,
        String mensagem,
        Long destinatarioId,
        String name,
        String tipoRemetente,
        Boolean lida) {
}
