package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Ensaio;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SolicitacaoAgendarEnsaioEvent implements NotificacaoEvent {
    private final Ensaio ensaio;
    private final List<Long> destinatarioIds;
}
