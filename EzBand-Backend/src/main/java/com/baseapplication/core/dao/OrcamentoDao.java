package com.baseapplication.core.dao;

import com.baseapplication.core.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrcamentoDao extends JpaRepository<Orcamento, Long> {
}
