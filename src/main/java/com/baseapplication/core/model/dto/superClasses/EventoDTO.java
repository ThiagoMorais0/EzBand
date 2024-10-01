package com.baseapplication.core.model.dto.superClasses;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
@NoArgsConstructor
@Getter
@Setter
public class EventoDTO {
    private Long id;
    private Long idBanda;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInclusao;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;
    private Time duracao;
    private Time horarioInicio;
    private String local;
    private String cidade;
    private String observacoes;
    private String status;
    private TipoEvento evento;

    public EventoDTO(Evento evento){
        BeanUtils.copyProperties(evento, this);
    }
}
