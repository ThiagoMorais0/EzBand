package com.baseapplication.core.dao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.MembroFantasmaEvento;
import com.baseapplication.core.model.MembroFantasmaEventoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface MembroFantasmaEventoDao extends JpaRepository<MembroFantasmaEvento, MembroFantasmaEventoId> {
    
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM membro_fantasma_evento mfe WHERE mfe.id_evento = :idEvento AND mfe.tipo_evento = :tipoEvento", nativeQuery = true)
    void removerTodosMembrosFantasma(Long idEvento, String tipoEvento);

//    @Query(value = "SELECT * FROM membro_fantasma_evento mfe WHERE mfe.id_evento = :idEvento AND mfe.tipo_evento = :tipoEvento", nativeQuery = true)
    @Query(value = "SELECT mfe FROM MembroFantasmaEvento mfe WHERE mfe.id.idEvento = :idEvento AND mfe.id.tipoEvento = :tipoEvento")
    List<MembroFantasmaEvento> buscarMembrosFantasmaPorEvento(Long idEvento, TipoEvento tipoEvento);
    
    List<MembroFantasmaEvento> findByIdIdEventoAndIdTipoEvento(Long idEvento, TipoEvento tipoEvento);
}
