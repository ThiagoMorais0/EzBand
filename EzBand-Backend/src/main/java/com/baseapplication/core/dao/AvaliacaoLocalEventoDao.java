package com.baseapplication.core.dao;

import com.baseapplication.core.model.AvaliacaoLocalEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvaliacaoLocalEventoDao extends JpaRepository<AvaliacaoLocalEvento, Long> {
    
    Optional<AvaliacaoLocalEvento> findByUsuarioIdAndLocalEventoId(Long usuarioId, Long localEventoId);
}
