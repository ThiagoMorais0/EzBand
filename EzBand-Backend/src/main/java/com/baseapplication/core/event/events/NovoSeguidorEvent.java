package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NovoSeguidorEvent implements NotificacaoEvent {
    private Usuario remetente;
    private Long idDestinatario;
}
