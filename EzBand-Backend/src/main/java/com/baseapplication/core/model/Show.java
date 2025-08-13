package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;

@Data
@Entity
@Table(name = "SHOW")
public class Show extends Evento {

    @ManyToOne
    @JoinColumn(name = "ID_LOCAL_EVENTO")
    private LocalEvento localEvento;

    private Time horarioPassagemSom;
    private BigDecimal valorContrato;
    private Boolean isPortaria;
    private Integer porcentagemPortaria;

    public Show(){
        this.setTipoEvento(TipoEvento.SHOW);
    }
}
