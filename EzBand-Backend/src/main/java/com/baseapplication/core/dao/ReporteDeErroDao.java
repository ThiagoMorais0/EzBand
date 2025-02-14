package com.baseapplication.core.dao;

import com.baseapplication.core.model.ReporteDeErro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteDeErroDao extends JpaRepository<ReporteDeErro, Long> {
}
