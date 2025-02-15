package com.baseapplication.core.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.MusicoBandaId;

@Repository
public interface MusicoBandaDao extends JpaRepository<MusicoBanda, MusicoBandaId> {
    @Query(value = "SELECT mb FROM MusicoBanda mb " +
            "WHERE mb.id.idBanda = :idBanda")
    List<MusicoBanda> buscarMembrosPorIdBanda(Long idBanda);
}
