package com.baseapplication.core.event.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SolicitarEntradaBandaEvent {
    private Long usuarioId;
    private Long bandaId;
}
