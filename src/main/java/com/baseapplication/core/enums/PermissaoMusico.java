package com.baseapplication.core.enums;

import lombok.Getter;

@Getter
public enum PermissaoMusico {
    MEMBRO_REGULAR(0, "Membro regular"),
    GERENCIADOR_DE_ENSAIOS(1, "Gerencia Ensaios"),
    GERENCIADOR_DE_SHOWS(2,"Gerencia Shows"),
    ADMINISTRADOR(3, "Administrador"),
    FUNDADOR(4, "Fundador");

    private final Integer nivel;
    private final String descricao;
    PermissaoMusico(Integer nivel, String descricao){
        this.nivel = nivel;
        this.descricao = descricao;
    }
}
