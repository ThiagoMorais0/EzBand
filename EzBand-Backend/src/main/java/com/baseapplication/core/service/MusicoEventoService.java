package com.baseapplication.core.service;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import java.util.List;

public interface MusicoEventoService {
    void removerTodosParticipantes(Evento evento);

    void salvar(MusicoEvento musicoEvento);

    MusicoEvento buscar(Long idEvento, TipoEvento tipoEvento, Long idUsuario);

    List<MusicoEvento> listarPorEvento(Long idEvento, TipoEvento tipoEvento);

    void remover(MusicoEvento musicoEvento);

    void remover(Long idEvento, TipoEvento tipoEvento, Long idUsuario);

    List<MusicoEvento> buscarMusicosPorEvento(Long idEvento, TipoEvento tipoEvento);
}
