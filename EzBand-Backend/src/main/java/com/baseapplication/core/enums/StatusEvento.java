package com.baseapplication.core.enums;

import lombok.Getter;

@Getter
public enum StatusEvento {
    PENDENTE("Pendente"),
    REALIZADO("Realizado"),
    CANCELADO("Cancelado"),
    AGUARDANDO_APROVACAO("Aguardando aprovação"),
    AGUARDANDO_APROVACAO_ORCAMENTO("Aguardando aprovação de orçamento");
    private final String descricao;

    StatusEvento(String descricao){
        this.descricao = descricao;
    }
}
