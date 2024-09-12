package com.baseapplication.core.model;

import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "MUSICO_EVENTO")
public class MusicoEvento {

    @EmbeddedId
    private MusicoEventoId id;
    private String instrumentos;
    private BigDecimal cache;
    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

}
