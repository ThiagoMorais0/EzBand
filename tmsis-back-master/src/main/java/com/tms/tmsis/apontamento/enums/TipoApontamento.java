package com.tms.tmsis.apontamento.enums;

import lombok.Getter;

@Getter
public enum TipoApontamento {
    INICIO("Início"),
    TERMINO("Término");

    private String descricao;

    TipoApontamento(String descricao){
        this.descricao = descricao;
    }
}
