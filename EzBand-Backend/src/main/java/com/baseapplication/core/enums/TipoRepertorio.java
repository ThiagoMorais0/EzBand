package com.baseapplication.core.enums;

import lombok.Getter;

/** Natureza do repertorio da banda: musicas proprias, de outros artistas ou as duas coisas. */
@Getter
public enum TipoRepertorio {
    AUTORAL("Autoral"),
    COVER("Cover"),
    MIX("Mix");

    private final String descricao;

    TipoRepertorio(String descricao) {
        this.descricao = descricao;
    }
}
