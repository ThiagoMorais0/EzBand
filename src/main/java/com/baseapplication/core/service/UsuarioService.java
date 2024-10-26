package com.baseapplication.core.service;

import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioPainelDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.model.Usuario;

public interface UsuarioService {

    Usuario findByLogin(String nome);
    Usuario findByEmail(String nome);

    void salvar(Usuario usuario);

    InfoUsuarioPainelDTO buscarInfoPainel(Long idUsuario);

    void atualizarInformacoesPerfil(InfoPerfilUsuarioDTO infoPerfil);

    Usuario buscarPorId(Long id);

    Usuario findByCelular(String celular);

    Usuario buscarPorContato(String contato, TipoContato tipoContato);
}
