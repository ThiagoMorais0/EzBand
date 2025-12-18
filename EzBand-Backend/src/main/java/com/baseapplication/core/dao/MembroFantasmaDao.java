package com.baseapplication.core.dao;

import com.baseapplication.core.model.MembroFantasma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembroFantasmaDao extends JpaRepository<MembroFantasma, Long> {
    
    @Query("SELECT m FROM MembroFantasma m WHERE m.idBanda = :idBanda")
    List<MembroFantasma> buscarPorIdBanda(Long idBanda);
}
