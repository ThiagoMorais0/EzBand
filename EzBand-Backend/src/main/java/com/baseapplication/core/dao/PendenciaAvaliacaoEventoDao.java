package com.baseapplication.core.dao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.PendenciaAvaliacaoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PendenciaAvaliacaoEventoDao extends JpaRepository<PendenciaAvaliacaoEvento, Long> {
    
    /**
     * Busca pendências não avaliadas de uma banda específica
     */
    @Query("""
        SELECT p FROM PendenciaAvaliacaoEvento p
        WHERE p.banda.id = :idBanda
        AND p.avaliado = false
        AND p.dataEvento IS NOT NULL
        AND (p.dataAdiamento IS NULL OR p.dataAdiamento < :dataAtual)
        ORDER BY p.dataEvento DESC
    """)
    List<PendenciaAvaliacaoEvento> buscarPendenciasPorBanda(
        @Param("idBanda") Long idBanda,
        @Param("dataAtual") LocalDateTime dataAtual
    );
    
    /**
     * Busca todas as pendências não avaliadas das bandas do usuário
     */
    @Query("""
        SELECT p FROM PendenciaAvaliacaoEvento p
        JOIN MusicoBanda mb ON mb.banda.id = p.banda.id
        WHERE mb.usuario.id = :idUsuario
        AND p.avaliado = false
        AND p.dataEvento IS NOT NULL
        AND (p.dataAdiamento IS NULL OR p.dataAdiamento < :dataAtual)
        ORDER BY p.dataEvento DESC
    """)
    List<PendenciaAvaliacaoEvento> buscarPendenciasPorUsuario(
        @Param("idUsuario") Long idUsuario,
        @Param("dataAtual") LocalDateTime dataAtual
    );
    
    /**
     * Conta pendências não avaliadas de uma banda
     */
    @Query("""
        SELECT COUNT(p) FROM PendenciaAvaliacaoEvento p
        WHERE p.banda.id = :idBanda
        AND p.avaliado = false
        AND (p.dataAdiamento IS NULL OR p.dataAdiamento < :dataAtual)
    """)
    Long contarPendenciasPorBanda(
        @Param("idBanda") Long idBanda,
        @Param("dataAtual") LocalDateTime dataAtual
    );
    
    /**
     * Conta pendências não avaliadas de todas as bandas do usuário
     */
    @Query("""
        SELECT COUNT(p) FROM PendenciaAvaliacaoEvento p
        JOIN MusicoBanda mb ON mb.banda.id = p.banda.id
        WHERE mb.usuario.id = :idUsuario
        AND p.avaliado = false
        AND (p.dataAdiamento IS NULL OR p.dataAdiamento < :dataAtual)
    """)
    Long contarPendenciasPorUsuario(
        @Param("idUsuario") Long idUsuario,
        @Param("dataAtual") LocalDateTime dataAtual
    );
    
    /**
     * Verifica se já existe uma pendência para um evento específico
     */
    Optional<PendenciaAvaliacaoEvento> findByIdEventoAndTipoEventoAndBandaIdAndAvaliadoFalse(
        Long idEvento, 
        TipoEvento tipoEvento, 
        Long bandaId
    );
    
    /**
     * Busca pendência específica por ID e banda
     */
    Optional<PendenciaAvaliacaoEvento> findByIdAndBandaId(Long id, Long bandaId);
}
