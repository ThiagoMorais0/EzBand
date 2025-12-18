package com.baseapplication.core.dao;

import com.baseapplication.core.model.AvaliacaoEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvaliacaoEstudioDao extends JpaRepository<AvaliacaoEstudio, Long> {
    
    Optional<AvaliacaoEstudio> findByUsuarioIdAndEstudioId(Long usuarioId, Long estudioId);
}
