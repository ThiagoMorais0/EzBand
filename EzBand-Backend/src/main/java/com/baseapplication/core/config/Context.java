package com.baseapplication.core.config;

import com.baseapplication.core.model.Usuario;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class Context {
    private static Usuario usuario;
//    private Empresa empresa;
}
