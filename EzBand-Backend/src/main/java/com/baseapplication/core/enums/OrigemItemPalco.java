package com.baseapplication.core.enums;

import lombok.Getter;

/**
 * De onde vem o item. E o unico campo que separa as duas listas do rider
 * ("a banda leva" x "esperamos do local"), sem o usuario preencher duas vezes.
 */
@Getter
public enum OrigemItemPalco {

    PROPRIO("A banda leva"),
    CASA("Esperamos do local"),
    ALUGADO("Alugado");

    private final String descricao;

    OrigemItemPalco(String descricao) {
        this.descricao = descricao;
    }
}
