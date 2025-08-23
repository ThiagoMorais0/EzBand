package com.baseapplication.core.enums;

public enum AcaoResposta {
    ACEITAR,
    RECUSAR;

    public static AcaoResposta findByName(String name) {
        for(AcaoResposta acao : AcaoResposta.values()){
            if(name.equals(acao.name())){
                return acao;
            }
        }
        return null;
    }
}
