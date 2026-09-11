package com.baseapplication.core.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PermissaoMusico {
    MEMBRO_REGULAR(0, "Membro regular"),
    GERENCIADOR_DE_ENSAIOS(1, "Gerencia Ensaios"),
    GERENCIADOR_DE_SHOWS(2, "Gerencia Shows"),
    ADMINISTRADOR(3, "Administrador"),
    GERENCIA_ORCAMENTOS(4, "Gerencia Orçamentos"),
    FUNDADOR(5, "Fundador"),
    GERENCIA_MAPA_PALCO(6, "Gerencia Mapa de Palco");

    private final Integer nivel;
    private final String descricao;

    PermissaoMusico(Integer nivel, String descricao) {
        this.nivel = nivel;
        this.descricao = descricao;
    }

    public static List<PermissaoMusico> getAll() {
        return Arrays.asList(PermissaoMusico.values());
    }
}
