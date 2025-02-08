package com.baseapplication.core.service;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.superClasses.Evento;

public interface MusicoEventoService {
    void removerTodosParticipantes(Evento evento);

    void salvar(MusicoEvento musicoEvento);

    MusicoEvento buscar(Long idEvento, TipoEvento tipoEvento, Long idUsuario);
}
