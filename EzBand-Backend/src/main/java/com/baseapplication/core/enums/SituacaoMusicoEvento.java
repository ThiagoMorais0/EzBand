package com.baseapplication.core.enums;

import lombok.Getter;

@Getter
public enum SituacaoMusicoEvento {
    ATIVO("Ativo"),
    CONVITE_PENDENTE("Convite Pendente"),
    INATIVO("Inativo"),
    RECUSADO("Recusado");

    private String descricao;

    SituacaoMusicoEvento(String descricao) {
        this.descricao = descricao;
    }
}
