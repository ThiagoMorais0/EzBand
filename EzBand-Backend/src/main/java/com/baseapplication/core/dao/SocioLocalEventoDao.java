package com.baseapplication.core.dao;

import com.baseapplication.core.model.SocioLocalEvento;
import com.baseapplication.core.model.SocioLocalEventoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocioLocalEventoDao extends JpaRepository<SocioLocalEvento, SocioLocalEventoId> {}
