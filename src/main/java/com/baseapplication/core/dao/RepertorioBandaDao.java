package com.baseapplication.core.dao;

import com.baseapplication.core.model.RepertorioBanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepertorioBandaDao extends JpaRepository<RepertorioBanda, Long> {
}
