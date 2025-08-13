package com.baseapplication.core.dao;

import com.baseapplication.core.model.LocalEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalEventoDao extends JpaRepository<LocalEvento, Long> {

    @Query(value = "SELECT e FROM LocalEvento e WHERE e.proprietario.id = :idProprietario")
    List<LocalEvento> findByProprietario(Long idProprietario);
}
