package com.baseapplication.core.dao;

import com.baseapplication.core.model.EquipamentoEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipamentoEstudioDao extends JpaRepository<EquipamentoEstudio, Long> {}
