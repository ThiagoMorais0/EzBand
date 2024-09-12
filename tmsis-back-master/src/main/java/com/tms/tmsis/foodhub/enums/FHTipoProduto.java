package com.tms.tmsis.foodhub.enums;

import java.util.Objects;

public enum FHTipoProduto {
    LANCHE(1, "Lanche"),
    PIZZA(2, "Pizza"),
    BEBIDA(3, "Bebida"),
    SOBREMESA(4, "Sobremesa");

    private final Integer codigo;
    private final String descricao;

    FHTipoProduto(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static FHTipoProduto findByCodigo(Integer codigo){
        for(FHTipoProduto tipo : FHTipoProduto.values()){
            if(Objects.equals(tipo.codigo, codigo)){
                return tipo;
            }
        }
        return null;
    }
}
