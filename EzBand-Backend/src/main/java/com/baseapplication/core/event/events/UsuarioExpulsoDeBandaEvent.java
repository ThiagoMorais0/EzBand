package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioExpulsoDeBandaEvent implements NotificacaoEvent {
    private Long idUsuario;
    private Long idBanda;
}
