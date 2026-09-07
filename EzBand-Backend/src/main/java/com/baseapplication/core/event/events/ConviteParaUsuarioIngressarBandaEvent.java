package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Banda;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConviteParaUsuarioIngressarBandaEvent implements NotificacaoEvent {
    private Long idUsuarioConvidado;
    private Banda banda;
    /** Eventos pendentes em que o convidado entra ao aceitar ("SHOW:12,ENSAIO:33"). */
    private String eventos;

    public ConviteParaUsuarioIngressarBandaEvent(Long idUsuarioConvidado, Banda banda) {
        this(idUsuarioConvidado, banda, null);
    }
}
