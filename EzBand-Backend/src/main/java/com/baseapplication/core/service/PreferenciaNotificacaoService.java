package com.baseapplication.core.service;

import com.baseapplication.core.dto.PreferenciaNotificacaoDTO;
import org.apache.coyote.BadRequestException;

import java.util.List;

public interface PreferenciaNotificacaoService {
    
    PreferenciaNotificacaoDTO salvarOuAtualizar(PreferenciaNotificacaoDTO dto) throws BadRequestException;
    
    PreferenciaNotificacaoDTO buscarPorBandaEUsuario(Long idBanda, Long idUsuario);
    
    PreferenciaNotificacaoDTO buscarPorBandaEMembroFantasma(Long idBanda, Long idMembroFantasma);
    
    List<PreferenciaNotificacaoDTO> buscarPorBanda(Long idBanda);
}
