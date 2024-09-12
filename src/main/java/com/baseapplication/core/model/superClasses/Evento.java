package com.baseapplication.core.model.superClasses;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.util.Date;

@MappedSuperclass
public abstract class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idBanda;
    @Column(name = "DATA_INCLUSAO")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date dataInclusao;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private Date data;
    private Time duracao;
    private Time horarioInicio;
    private String local;
    private String cidade;
    private String observacoes;
    @Enumerated(EnumType.STRING)
    private StatusEvento status;
    private TipoEvento evento;

}
