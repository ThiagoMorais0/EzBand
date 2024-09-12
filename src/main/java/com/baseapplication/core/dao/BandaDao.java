package com.baseapplication.core.dao;

import com.baseapplication.core.model.Banda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BandaDao extends JpaRepository<Banda, Long> {
    @Query("SELECT b FROM Banda b INNER JOIN MusicoBanda m ON m.id.idBanda = b.id WHERE m.id.idUsuario = :idUsuario")
    Optional<List<Banda>> buscarBandasPorUsuario(Long idUsuario);
}
