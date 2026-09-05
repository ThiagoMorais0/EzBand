package com.baseapplication.core.event.events;

import com.baseapplication.core.enums.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SessaoPalcoIniciadaEvent implements NotificacaoEvent {
    private final Long idEvento;
    private final TipoEvento tipoEvento;
    private final Long idBanda;
    private final String nomeBanda;
    private final String urlLogoBanda;
    private final String local;
    private final Long idIniciador;
    private final String nomeIniciador;
    private final List<Long> destinatarioIds;
}
