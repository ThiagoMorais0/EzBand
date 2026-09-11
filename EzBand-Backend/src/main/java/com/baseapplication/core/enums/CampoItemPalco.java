package com.baseapplication.core.enums;

import lombok.Getter;

/**
 * Campos opcionais que um item tecnico pode expor. Cada TipoItemPalco declara
 * quais se aplicam a ele, e o front monta o formulario a partir dessa lista --
 * assim acrescentar um tipo novo nao exige mexer no Vue.
 */
@Getter
public enum CampoItemPalco {

    MARCA_MODELO("Marca / modelo", "texto"),
    ROTULO("Descrição", "texto"),
    VOLTAGEM("Voltagem", "voltagem"),
    VIAS("Vias", "numero"),
    MIX_INDEPENDENTE("Mix independente", "booleano"),
    PONTO("Onde", "texto"),
    CANAIS("Canais de mesa", "numero");

    private final String descricao;
    private final String tipoCampo;

    CampoItemPalco(String descricao, String tipoCampo) {
        this.descricao = descricao;
        this.tipoCampo = tipoCampo;
    }
}
