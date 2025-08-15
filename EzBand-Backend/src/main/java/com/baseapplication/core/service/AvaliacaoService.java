package com.baseapplication.core.service;

import com.baseapplication.core.dto.AvaliacaoDTO;
import com.baseapplication.core.enums.TipoAvaliacao;

public interface AvaliacaoService {
    void avaliar(AvaliacaoDTO avaliacao);

    Double buscarMedia(Long id, TipoAvaliacao tipo);
}
