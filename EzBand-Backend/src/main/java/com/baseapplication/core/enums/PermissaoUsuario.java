package com.baseapplication.core.enums;

import java.util.Arrays;
import java.util.List;

public enum PermissaoUsuario {
    ADMIN("admin"),
    USUARIO("usuario");

    private String role;

    PermissaoUsuario(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
    public static List<PermissaoUsuario> getAll(){
        return Arrays.asList(PermissaoUsuario.values());
    }
}
