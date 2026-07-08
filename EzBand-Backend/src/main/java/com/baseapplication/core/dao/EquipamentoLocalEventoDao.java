package com.baseapplication.core.dao;

import com.baseapplication.core.model.EquipamentoLocalEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipamentoLocalEventoDao extends JpaRepository<EquipamentoLocalEvento, Long> {}
