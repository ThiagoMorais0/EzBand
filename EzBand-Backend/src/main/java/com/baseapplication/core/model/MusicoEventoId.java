package com.baseapplication.core.model;

import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MusicoEventoId implements Serializable {

    @Column(name = "ID_EVENTO")
    private Long idEvento;

    @Column(name = "ID_USUARIO", insertable = false, updatable = false)
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;
}
