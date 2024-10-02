package com.baseapplication.core.service;

import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;

import java.util.List;

public interface MusicoBandaService {
    MusicoBanda buscarPorIdUsuarioEIdBanda(Long idUsuario, Long idBanda);

    MusicoBanda cadastrarUsuarioEmBanda(Long idUsuario, Long idBanda, String instrumentos);

    void expulsar(Long idBanda, Long idUsuario);

    List<Usuario> buscarMembrosPorIdBanda(Long idBanda);
}
