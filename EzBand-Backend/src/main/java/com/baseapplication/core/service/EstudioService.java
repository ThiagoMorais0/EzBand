package com.baseapplication.core.service;

import com.baseapplication.core.dto.EstudioDTO;
import com.baseapplication.core.model.Estudio;

import java.util.List;

public interface EstudioService {
    void cadastrar(Estudio toEntity);

    List<Estudio> buscarEstudiosDoUsuario();

    List<Estudio> buscarTodos();

    void deletarTodos();

    Estudio buscarPorId(Long id);

    void editar(EstudioDTO estudioDTO);
}
