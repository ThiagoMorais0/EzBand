package com.baseapplication.core.service;

import com.baseapplication.core.model.RepertorioBanda;

import java.util.List;

public interface RepertorioBandaService {
    void salvar(RepertorioBanda repertorioBanda);

    RepertorioBanda buscarPorId(Long id);

    void deletar(RepertorioBanda repertorioBanda);

    Integer buscarUltimoIndice(Long idBanda);

    void updateIndice(Long id, Integer indice);
}
