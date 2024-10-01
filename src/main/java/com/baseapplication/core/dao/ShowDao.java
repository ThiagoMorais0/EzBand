package com.baseapplication.core.dao;

import com.baseapplication.core.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowDao extends JpaRepository<Show, Long> {
    @Query(value = "SELECT * FROM MusicoEvento me " +
            "INNER JOIN Show s on me.id.idEvento = s.id " +
            "WHERE me.tipoEvento = 'SHOW' AND " +
            "me.id.idUsuario = :idUsuario")
    List<Show> buscarPorIdUsuario(Long idUsuario);
}
