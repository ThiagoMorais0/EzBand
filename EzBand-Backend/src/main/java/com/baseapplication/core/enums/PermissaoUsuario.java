package com.baseapplication.core.enums;

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
}
