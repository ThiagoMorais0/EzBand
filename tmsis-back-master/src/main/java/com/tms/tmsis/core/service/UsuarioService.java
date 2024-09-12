package com.tms.tmsis.core.service;

import com.tms.tmsis.core.dto.RetornoDTO;
import com.tms.tmsis.core.model.Usuario;

public interface UsuarioService {

    Usuario findByLogin(String nome);
    Usuario findByEmail(String nome);

    void salvar(Usuario usuario);

}
