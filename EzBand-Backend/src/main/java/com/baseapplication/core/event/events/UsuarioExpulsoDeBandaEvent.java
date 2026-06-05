package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Banda;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioExpulsoDeBandaEvent implements NotificacaoEvent {
    private Long idUsuario;
    private Banda banda;
}
