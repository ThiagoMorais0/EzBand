package com.baseapplication.core.dto;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
public class EventoPendenteConviteDTO {

    private Long id;
    private TipoEvento tipoEvento;
    private StatusEvento status;
    private String data;
    private Time horarioInicio;
    private String local;

    public EventoPendenteConviteDTO(Evento evento) {
        this.id = evento.getId();
        this.tipoEvento = evento.getTipoEvento();
        this.status = evento.getStatus();
        this.data = evento.getData() != null ? DateUtils.localDateToString(evento.getData()) : null;
        this.horarioInicio = evento.getHorarioInicio();
        this.local = resolverLocal(evento);
    }

    private String resolverLocal(Evento evento) {
        if (evento instanceof Show show && show.getLocalEvento() != null && show.getLocalEvento().getNome() != null) {
            return show.getLocalEvento().getNome();
        }
        if (evento instanceof Ensaio ensaio && ensaio.getEstudio() != null && ensaio.getEstudio().getNome() != null) {
            return ensaio.getEstudio().getNome();
        }
        return evento.getLocal();
    }
}
