package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "ENSAIO")
public class Ensaio extends Evento {

    @ManyToOne
    @JoinColumn(name = "ID_ESTUDIO")
    private Estudio estudio;

    private BigDecimal valor;

    public Ensaio(){
        this.setTipoEvento(TipoEvento.ENSAIO);
    }
}
