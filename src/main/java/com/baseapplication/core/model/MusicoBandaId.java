package com.baseapplication.core.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MusicoBandaId {

    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "ID_BANDA", insertable = false, updatable = false)
    private Long idBanda;

}
