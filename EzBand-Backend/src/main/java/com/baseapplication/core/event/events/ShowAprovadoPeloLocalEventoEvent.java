package com.baseapplication.core.event.events;

import com.baseapplication.core.model.Show;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShowAprovadoPeloLocalEventoEvent implements NotificacaoEvent {
    private final Show show;
}
