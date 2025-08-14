package com.baseapplication.core.service;

import com.baseapplication.core.dto.NovoOrcamentoDTO;
import com.baseapplication.core.model.dto.ParametroCustoDTO;

import java.util.List;

public interface OrcamentoService {
    List<ParametroCustoDTO> buscarParametrosCusto(Long idBanda);

    void salvarParametrosCusto(List<ParametroCustoDTO> dto);

    void deletarParametroCusto(ParametroCustoDTO dto);

    void gerarOrcamento(NovoOrcamentoDTO novoOrcamento);
}
