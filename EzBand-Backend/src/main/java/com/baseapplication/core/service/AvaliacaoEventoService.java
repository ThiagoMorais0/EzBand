package com.baseapplication.core.service;

import com.baseapplication.core.dto.AvaliacaoEventoDTO;
import com.baseapplication.core.dto.PendenciaAvaliacaoEventoDTO;
import com.baseapplication.core.dto.ResumoPendenciasAvaliacaoDTO;

import java.util.List;

public interface AvaliacaoEventoService {
    
    /**
     * Busca todas as pendências de avaliação de uma banda
     */
    List<PendenciaAvaliacaoEventoDTO> buscarPendenciasPorBanda(Long idBanda);
    
    /**
     * Busca todas as pendências de avaliação das bandas do usuário logado
     */
    ResumoPendenciasAvaliacaoDTO buscarPendenciasDoUsuario();
    
    /**
     * Adia uma pendência de avaliação
     */
    void adiarPendencia(Long idPendencia);
    
    /**
     * Registra uma avaliação de evento
     */
    AvaliacaoEventoDTO avaliarEvento(AvaliacaoEventoDTO avaliacaoDTO);
    
    /**
     * Busca avaliações de um evento específico
     */
    List<AvaliacaoEventoDTO> buscarAvaliacoesDoEvento(Long idEvento, String tipoEvento);
    
    /**
     * Busca avaliações de uma banda
     */
    List<AvaliacaoEventoDTO> buscarAvaliacoesDaBanda(Long idBanda);
    
    /**
     * Conta pendências não avaliadas do usuário
     */
    Long contarPendenciasDoUsuario();
}
