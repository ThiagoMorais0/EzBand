package com.baseapplication.core.enums;

public enum StatusNotificacao {
    VISUALIZADO("Visualizado"),
    ACEITO("Aceito"),
    RECUSADO("Recusado");

    private final String descricao;
    StatusNotificacao(String descricao){
        this.descricao = descricao;
    }
}
