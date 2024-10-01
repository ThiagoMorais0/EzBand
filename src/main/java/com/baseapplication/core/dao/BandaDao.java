package com.baseapplication.core.dao;

import com.baseapplication.core.model.Banda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface BandaDao extends JpaRepository<Banda, Long> {
    @Query("SELECT b FROM Banda b INNER JOIN MusicoBanda m ON m.id.idBanda = b.id WHERE m.id.idUsuario = :idUsuario")
    Optional<List<Banda>> buscarBandasPorUsuario(Long idUsuario);

    @Query(value = "SELECT DISTINCT b from Banda b " +
            "INNER JOIN Show s on s.banda.id = b.id " +
            "INNER JOIN MusicoEvento me on me.show.id = s.id " +
            "WHERE me.usuario.id = :idUsuario " +
            "AND s.banda.id NOT IN (SELECT m.banda.id FROM MusicoBanda m where m.usuario.id = :idUsuario)")
    Optional<List<Banda>> buscarParticipacoesEspeciais(Long idUsuario);
}
