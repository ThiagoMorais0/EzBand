package com.baseapplication.core.model;

import com.baseapplication.core.enums.PermissaoMusico;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

public class MusicoEventoId {

    @Column(name = "ID_EVENTO")
    private Long idEvento;

    @Column(name = "ID_USUARIO", insertable = false, updatable = false)
    private Long idUsuario;
}
