package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
public class CalendarioEventoDTO {
    private Long id;
    private String tipo;
    private String data;
    private Time horarioInicio;
    private String local;
    private Long idBanda;
    private String nomeBanda;
    private String urlLogoBanda;
    private String corHex;

    public CalendarioEventoDTO(Evento evento, String corHex) {
        this.id = evento.getId();
        this.tipo = evento.getTipoEvento().name();
        this.data = DateUtils.localDateToString(evento.getData());
        this.horarioInicio = evento.getHorarioInicio();
        this.local = evento.getLocal();
        if (evento.getBanda() != null) {
            this.idBanda = evento.getBanda().getId();
            this.nomeBanda = evento.getBanda().getNome();
            this.urlLogoBanda = evento.getBanda().getUrlLogo();
        }
        this.corHex = corHex;
    }
}
