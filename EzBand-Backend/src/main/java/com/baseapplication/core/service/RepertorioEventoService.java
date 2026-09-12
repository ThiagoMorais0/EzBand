package com.baseapplication.core.service;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.RepertorioEvento;

import java.util.List;

public interface RepertorioEventoService {
    void salvarLista(List<RepertorioEvento> repertorio);

    void limparRepertorioEvento(Long idEvento, TipoEvento tipoEvento);

    List<RepertorioEvento> buscarPorEvento(Long idEvento, TipoEvento tipoEvento);

    List<RepertorioEvento> buscarPorEventos(Long idBanda, TipoEvento tipoEvento, List<Long> idsEventos);

    RepertorioEvento buscarPorIndiceEEvento(Integer indice, Long idEvento, TipoEvento tipoEvento);

    void salvar(RepertorioEvento repertorioEvento);

    void removerMusicaDoRepertorio(Long idEvento, TipoEvento tipoEvento, Integer indice);
}
