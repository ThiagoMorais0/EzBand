package com.baseapplication.core.enums;

import lombok.Getter;

/**
 * Agrupamento dos itens tecnicos na ficha da posicao e no rider.
 * A ordem do enum e a ordem em que as secoes aparecem nas duas telas.
 */
@Getter
public enum CategoriaItemPalco {

    FONTE_SOM(0, "Fonte de som"),
    MONITORACAO(1, "Monitoração"),
    ENERGIA(2, "Energia"),
    ESTRUTURA(3, "Estrutura");

    private final Integer ordem;
    private final String descricao;

    CategoriaItemPalco(Integer ordem, String descricao) {
        this.ordem = ordem;
        this.descricao = descricao;
    }
}
