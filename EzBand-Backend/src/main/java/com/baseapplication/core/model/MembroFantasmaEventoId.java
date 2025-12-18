package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class MembroFantasmaEventoId implements Serializable {
    
    @Column(name = "ID_EVENTO")
    private Long idEvento;
    
    @Column(name = "ID_MEMBRO_FANTASMA")
    private Long idMembroFantasma;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_EVENTO")
    private TipoEvento tipoEvento;
}
