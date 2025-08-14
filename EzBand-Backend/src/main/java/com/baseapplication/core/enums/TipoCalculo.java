package com.baseapplication.core.enums;

public enum TipoCalculo {
    POR_MUSICO,
    POR_DIA,
    POR_KM,
    VALOR_FIXO;

    public static TipoCalculo findByNome(String nome){
        if (nome == null) return null;
        for (TipoCalculo tipo : TipoCalculo.values()) {
            if (tipo.name().equalsIgnoreCase(nome)) {
                return tipo;
            }
        }
        return null;
    }
}
