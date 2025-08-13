package com.baseapplication.core.service;

import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.model.LocalEvento;

import java.util.List;

public interface LocalEventoService {
    void cadastrar(LocalEvento toEntity);

    List<LocalEvento> buscarLocalEventosDoUsuario();

    List<LocalEvento> buscarTodos();

    void deletarTodos();

    LocalEvento buscarPorId(Long id);

    void editar(LocalEventoDTO estudioDTO);
}
