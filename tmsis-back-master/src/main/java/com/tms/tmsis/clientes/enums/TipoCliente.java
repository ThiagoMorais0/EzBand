package com.tms.tmsis.clientes.enums;

import lombok.Getter;

@Getter
public enum TipoCliente {
    FISICA(1, "Física"),
    JURIDICA(2, "Jurídica");

    private Integer codigo;
    private String descricao;

    TipoCliente(Integer codigo, String descricao){
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public static TipoCliente findByCodigo(Integer codigo){
        for(TipoCliente tipo : TipoCliente.values()){
            if(tipo.codigo.equals(codigo)){
                return tipo;
            }
        }
        return null;
    }
}
