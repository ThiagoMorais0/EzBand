package com.baseapplication.core.service;

import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioPainelDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.model.Usuario;

public interface UsuarioHelperService {
    Usuario buscarPorContato(String contato, TipoContato tipoContato);

    Usuario buscarPorId(Long idUsuario);
}
