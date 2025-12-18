package com.baseapplication.core.service;

import com.baseapplication.core.dto.ResultadoSugestaoRepertorioDTO;
import com.baseapplication.core.dto.SugestaoRepertorioDTO;

public interface SugestaoRepertorioService {
    
    /**
     * Gera uma sugestão de repertório baseada em:
     * - Duração do show
     * - Relevância das músicas (prioriza músicas com alta relevância)
     * - Posição no show (distribui músicas de início, meio e fim)
     * - Duração das músicas (não ultrapassa o tempo do show)
     */
    ResultadoSugestaoRepertorioDTO sugerirRepertorio(SugestaoRepertorioDTO dto);
}
