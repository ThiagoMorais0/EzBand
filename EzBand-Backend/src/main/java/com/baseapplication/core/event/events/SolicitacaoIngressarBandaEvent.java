package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SolicitacaoIngressarBandaEvent implements NotificacaoEvent {
    private Usuario usuario;
    private Long idBanda;
    private String instrumento;
}
