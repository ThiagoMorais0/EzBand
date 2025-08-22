package com.baseapplication.core.event.events;

import com.baseapplication.core.interfaces.Evento;
import com.baseapplication.core.model.Ensaio;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SolicitacaoAgendarEnsaioEvent implements NotificacaoEvent{
    private final Ensaio ensaio;
}
