package com.baseapplication.core.model.dto.superClasses;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;

public abstract class EventoDTO {
    private Long id;
    private Long idBanda;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dataInclusao;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date data;
    private Time duracao;
    private Time horarioInicio;
    private String local;
    private String cidade;
    private String observacoes;
    private String status;
    private TipoEvento evento;
}
