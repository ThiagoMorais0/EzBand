package com.baseapplication.core.model;

import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.enums.SituacaoMusicoEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
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

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "id_evento", referencedColumnName = "id", insertable = false, updatable = false),
            @JoinColumn(name = "tipo_evento", referencedColumnName = "tipoEvento", insertable = false, updatable = false)
    })
    private Evento evento;


    @Enumerated(EnumType.STRING)
    private SituacaoMusicoEvento situacao;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "id")
    private Usuario usuario;


}
