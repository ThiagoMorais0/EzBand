package com.baseapplication.core.service;

import com.baseapplication.core.dto.CustoOperacionalDTO;
import com.baseapplication.core.dto.CustoOperacionalRequestDTO;

import java.util.List;

public interface CustoOperacionalService {

    List<CustoOperacionalDTO> buscarPorShow(Long idShow);

    CustoOperacionalDTO adicionar(CustoOperacionalRequestDTO dto);

    CustoOperacionalDTO atualizar(Long id, CustoOperacionalRequestDTO dto);

    void remover(Long id);
}
