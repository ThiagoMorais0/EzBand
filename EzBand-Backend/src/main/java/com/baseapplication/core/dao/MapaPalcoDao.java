package com.baseapplication.core.dao;

import com.baseapplication.core.model.MapaPalco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MapaPalcoDao extends JpaRepository<MapaPalco, Long> {

    @Query("SELECT m FROM MapaPalco m WHERE m.idBanda = :idBanda AND m.ativo = true "
            + "ORDER BY m.padrao DESC, m.nome ASC")
    List<MapaPalco> buscarPorIdBanda(Long idBanda);

    @Query("SELECT m FROM MapaPalco m WHERE m.idBanda = :idBanda AND m.padrao = true AND m.ativo = true")
    Optional<MapaPalco> buscarPadraoDaBanda(Long idBanda);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE MapaPalco m SET m.padrao = false WHERE m.idBanda = :idBanda")
    void limparPadraoDaBanda(Long idBanda);

    @Query("SELECT COUNT(m) FROM MapaPalco m WHERE m.idBanda = :idBanda AND m.ativo = true")
    long contarPorIdBanda(Long idBanda);
}
