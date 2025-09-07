package com.baseapplication.core.event.events;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ConviteParaMusicoEventoEvent implements NotificacaoEvent {
    private Long idUsuarioConvidado;
    private Evento evento;
    private BigDecimal cache;
    private String instrumentos;
}
