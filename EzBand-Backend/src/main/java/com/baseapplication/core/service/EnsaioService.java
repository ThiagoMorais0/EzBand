package com.baseapplication.core.service;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.superClasses.Evento;

import java.util.List;

public interface EnsaioService {
    Ensaio buscarPorId(Long idEvento);

    List<Ensaio> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario);

    void salvar(Ensaio ensaio);

    List<Ensaio> buscarEnsaiosPorStatusBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario, String string);

    void alterarStatus(Long idEvento, StatusEvento novoStatus);
}
