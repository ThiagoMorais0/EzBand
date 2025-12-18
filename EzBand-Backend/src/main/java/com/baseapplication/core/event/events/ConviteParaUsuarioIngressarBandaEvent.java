package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Banda;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConviteParaUsuarioIngressarBandaEvent implements NotificacaoEvent {
    private Long idUsuarioConvidado;
    private Banda banda;
}
