package com.baseapplication.core.dao;

import com.baseapplication.core.model.CondicaoOrcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CondicaoOrcamentoDao extends JpaRepository<CondicaoOrcamento, Long> {
}
