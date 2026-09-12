package com.baseapplication.core.dao;

import com.baseapplication.core.model.ItemMapaPalco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemMapaPalcoDao extends JpaRepository<ItemMapaPalco, Long> {

    @Query("SELECT i FROM ItemMapaPalco i WHERE i.idMapaPalco = :idMapaPalco "
            + "ORDER BY i.ordem ASC, i.id ASC")
    List<ItemMapaPalco> buscarPorIdMapaPalco(Long idMapaPalco);

    @Query("SELECT i FROM ItemMapaPalco i WHERE i.idPosicao = :idPosicao ORDER BY i.ordem ASC, i.id ASC")
    List<ItemMapaPalco> buscarPorIdPosicao(Long idPosicao);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM ItemMapaPalco i WHERE i.idPosicao = :idPosicao")
    void deletarPorIdPosicao(Long idPosicao);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM ItemMapaPalco i WHERE i.idMapaPalco = :idMapaPalco AND i.idPosicao IS NULL")
    void deletarGeraisPorIdMapaPalco(Long idMapaPalco);
}
