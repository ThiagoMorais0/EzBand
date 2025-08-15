package com.baseapplication.core.dao;

import com.baseapplication.core.model.AvaliacaoEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaliacaoEstudioDao extends JpaRepository<AvaliacaoEstudio, Long> {
}
