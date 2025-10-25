package com.baseapplication.core.event.events.resposta;

import com.baseapplication.core.event.events.NotificacaoEvent;
import com.baseapplication.core.model.superClasses.Notificacao;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RespostaSolicitacaoAgendarShowEvent implements NotificacaoEvent {
    private final Notificacao resposta;
}
