package com.baseapplication.core.dao;

import com.baseapplication.core.model.ServicoEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicoEstudioDao extends JpaRepository<ServicoEstudio, Long> {}
