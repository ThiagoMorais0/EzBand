package com.baseapplication.core.dao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.AvaliacaoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AvaliacaoEventoDao extends JpaRepository<AvaliacaoEvento, Long> {
    
    /**
     * Busca avaliação de um evento específico por uma banda
     */
    Optional<AvaliacaoEvento> findByIdEventoAndTipoEventoAndBandaId(
        Long idEvento, 
        TipoEvento tipoEvento, 
        Long bandaId
    );
    
    /**
     * Busca todas as avaliações de uma banda
     */
    List<AvaliacaoEvento> findByBandaIdOrderByDataAvaliacaoDesc(Long bandaId);
    
    /**
     * Busca todas as avaliações de um evento específico
     */
    List<AvaliacaoEvento> findByIdEventoAndTipoEventoOrderByDataAvaliacaoDesc(
        Long idEvento, 
        TipoEvento tipoEvento
    );
    
    /**
     * Calcula média de avaliações de experiência geral de uma banda
     */
    @Query("""
        SELECT AVG(a.experienciaGeral) 
        FROM AvaliacaoEvento a
        WHERE a.banda.id = :idBanda
    """)
    Double calcularMediaExperienciaGeralBanda(@Param("idBanda") Long idBanda);
    
    /**
     * Calcula média de avaliações de um local de evento
     */
    @Query("""
        SELECT AVG((COALESCE(a.qualidadeSom, 0) + COALESCE(a.estrutura, 0) + 
                    COALESCE(a.organizacao, 0) + COALESCE(a.atendimento, 0)) / 4.0)
        FROM AvaliacaoEvento a
        JOIN Show s ON s.id = a.idEvento
        WHERE a.tipoEvento = 'SHOW'
        AND s.localEvento.id = :idLocalEvento
        AND (a.qualidadeSom IS NOT NULL OR a.estrutura IS NOT NULL OR 
             a.organizacao IS NOT NULL OR a.atendimento IS NOT NULL)
    """)
    Double calcularMediaAvaliacaoLocalEvento(@Param("idLocalEvento") Long idLocalEvento);
    
    /**
     * Calcula média de avaliações de um estúdio
     */
    @Query("""
        SELECT AVG((COALESCE(a.qualidadeSom, 0) + COALESCE(a.estrutura, 0) + 
                    COALESCE(a.organizacao, 0) + COALESCE(a.atendimento, 0)) / 4.0)
        FROM AvaliacaoEvento a
        JOIN Ensaio e ON e.id = a.idEvento
        WHERE a.tipoEvento = 'ENSAIO'
        AND e.estudio.id = :idEstudio
        AND (a.qualidadeSom IS NOT NULL OR a.estrutura IS NOT NULL OR 
             a.organizacao IS NOT NULL OR a.atendimento IS NOT NULL)
    """)
    Double calcularMediaAvaliacaoEstudio(@Param("idEstudio") Long idEstudio);
}
