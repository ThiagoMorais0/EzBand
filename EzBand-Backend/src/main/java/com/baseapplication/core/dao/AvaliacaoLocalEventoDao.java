package com.baseapplication.core.dao;

import com.baseapplication.core.model.AvaliacaoLocalEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoLocalEventoDao extends JpaRepository<AvaliacaoLocalEvento, Long> {
}
