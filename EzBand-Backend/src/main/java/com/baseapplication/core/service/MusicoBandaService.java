package com.baseapplication.core.service;

import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;

import java.util.List;

public interface MusicoBandaService {
    MusicoBanda buscarPorIdUsuarioEIdBanda(Long idUsuario, Long idBanda);

    MusicoBanda cadastrarUsuarioEmBanda(Usuario usuario, Banda banda, String instrumentos);

    void expulsar(Long idBanda, Long idUsuario);

    List<MusicoBanda> buscarMembrosPorIdBanda(Long idBanda);
}
