package com.baseapplication.core.service;

import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.superClasses.Evento;

import java.time.LocalDate;
import java.util.List;

public interface ShowService {
    Show buscarPorId(Long idEvento);

    List<Show> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario);

    void salvar(Show show);

    List<RepertorioEvento> buscarRepertorio(Long idEvento);

    List<Show> buscarShowsPendentesPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario);

    List<Show> buscarShowsAguardandoPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario);
    List<Show> buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario, String status);

    Evento buscarPrimeiroPorUsuarioEData(Long idUsuario, LocalDate data);
}
