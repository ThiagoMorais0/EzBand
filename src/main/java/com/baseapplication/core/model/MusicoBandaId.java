package com.baseapplication.core.model;

import jakarta.persistence.Column;

public class MusicoBandaId {

    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "ID_BANDA", insertable = false, updatable = false)
    private Long idBanda;

}
