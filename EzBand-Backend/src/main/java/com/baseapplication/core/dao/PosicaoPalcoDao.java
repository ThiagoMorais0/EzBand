package com.baseapplication.core.dao;

import com.baseapplication.core.model.PosicaoPalco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PosicaoPalcoDao extends JpaRepository<PosicaoPalco, Long> {

    @Query("SELECT p FROM PosicaoPalco p WHERE p.idMapaPalco = :idMapaPalco "
            + "ORDER BY p.linha ASC, p.posX ASC")
    List<PosicaoPalco> buscarPorIdMapaPalco(Long idMapaPalco);

    @Query("SELECT p FROM PosicaoPalco p WHERE p.idMapaPalco IN :idsMapaPalco "
            + "ORDER BY p.linha ASC, p.posX ASC")
    List<PosicaoPalco> buscarPorIdsMapaPalco(List<Long> idsMapaPalco);

    @Query("SELECT COUNT(p) FROM PosicaoPalco p WHERE p.idMapaPalco = :idMapaPalco "
            + "AND p.dataPreenchimento IS NULL")
    long contarPendentes(Long idMapaPalco);
}
