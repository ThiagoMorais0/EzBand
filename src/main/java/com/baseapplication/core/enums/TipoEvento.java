package com.baseapplication.core.enums;

import lombok.Getter;

@Getter
public enum TipoEvento {
    SHOW("Show"),
    ENSAIO("Ensaio");

    private final String descricao;
    TipoEvento(String descricao){
        this.descricao = descricao;
    }

}
