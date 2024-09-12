package com.tms.tmsis.apontamento.service;

import com.tms.tmsis.apontamento.dto.ApontamentoDTO;
import com.tms.tmsis.core.dto.RetornoDTO;

public interface ApontamentoService {
    RetornoDTO registrarApontamento(ApontamentoDTO apontamento);
}
