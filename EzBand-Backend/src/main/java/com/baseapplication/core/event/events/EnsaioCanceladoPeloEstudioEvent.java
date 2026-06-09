package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.Estudio;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EnsaioCanceladoPeloEstudioEvent implements NotificacaoEvent {
    private final Ensaio ensaio;
    private final Estudio estudio;
    private final String motivo;
}
