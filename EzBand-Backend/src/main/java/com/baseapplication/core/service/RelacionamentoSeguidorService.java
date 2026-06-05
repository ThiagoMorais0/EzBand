package com.baseapplication.core.service;

import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.enums.TipoRelacionamento;
import com.baseapplication.core.model.Usuario;

import java.util.List;

public interface RelacionamentoSeguidorService {
    TipoRelacionamento buscarTipoRelacionamento(Long idUsuarioLogado, Long idUsuarioBuscado);
    void seguirUsuario(Long idUsuarioASeguir);
    void deixarDeSeguir(Long idUsuario);
    List<Usuario> buscarAmigos();
    List<Usuario> buscarSeguidores(Long idUsuario);
    Long contarSeguidores(Long idUsuario);
    Long contarSeguindo(Long idUsuario);
}
