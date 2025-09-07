package com.baseapplication.core.service;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.RepertorioEvento;

import java.util.List;

public interface RepertorioEventoService {
    void salvarLista(List<RepertorioEvento> repertorio);

    void limparRepertorioEvento(Long idEvento, TipoEvento tipoEvento);

    List<RepertorioEvento> buscarPorEvento(Long idEvento, TipoEvento tipoEvento);

    RepertorioEvento buscarPorIndiceEEvento(Integer indice, Long idEvento, TipoEvento tipoEvento);

    void salvar(RepertorioEvento repertorioEvento);
}
