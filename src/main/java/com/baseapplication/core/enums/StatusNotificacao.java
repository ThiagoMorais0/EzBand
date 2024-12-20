package com.baseapplication.core.enums;

public enum StatusNotificacao {

    NAO_VISUALIZADO("Visualizado"),
    VISUALIZADO("Visualizado"),
    ACEITO("Aceito"),
    RECUSADO("Recusado");

    private final String descricao;
    StatusNotificacao(String descricao){
        this.descricao = descricao;
    }
}
