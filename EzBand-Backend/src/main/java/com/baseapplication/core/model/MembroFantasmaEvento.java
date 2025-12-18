package com.baseapplication.core.model;

import com.baseapplication.core.model.superClasses.Evento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "MEMBRO_FANTASMA_EVENTO")
public class MembroFantasmaEvento {

    @EmbeddedId
    private MembroFantasmaEventoId id;
    
    private String instrumentos;
    
    private BigDecimal cache;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "id_evento", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "tipo_evento", referencedColumnName = "tipoEvento", insertable = false, updatable = false)
    })
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "ID_MEMBRO_FANTASMA", referencedColumnName = "id", insertable = false, updatable = false)
    private MembroFantasma membroFantasma;
}
