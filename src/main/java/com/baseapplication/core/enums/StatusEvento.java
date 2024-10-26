package com.baseapplication.core.enums;

import lombok.Getter;

@Getter
public enum StatusEvento {
    PENDENTE("Pendente"),
    REALIZADO("Realizado"),
    CANCELADO("Cancelado"),
    AGUARDANDO_APROVACAO("Aguardando aprovação");
    private final String descricao;

    StatusEvento(String descricao){
        this.descricao = descricao;
    }
}
