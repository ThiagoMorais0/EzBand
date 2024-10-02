package com.baseapplication.core.dao;

import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.MusicoBandaId;
import com.baseapplication.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicoBandaDao extends JpaRepository<MusicoBanda, MusicoBandaId> {
    @Query(value = "SELECT mb.usuario FROM MusicoBanda mb " +
            "WHERE mb.id.idBanda = :idBanda")
    List<Usuario> buscarMembrosPorIdBanda(Long idBanda);
}
