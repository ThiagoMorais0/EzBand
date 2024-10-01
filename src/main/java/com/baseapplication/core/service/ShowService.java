package com.baseapplication.core.service;

import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.superClasses.Evento;

import java.util.List;

public interface ShowService {
    Show buscarPorId(Long idEvento);

    List<Show> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario);
}
