package com.baseapplication.core.event.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConviteParaUsuarioIngressarBandaEvent implements NotificacaoEvent {
    private Long idUsuarioConvidado;
    private Long idBanda;
}
