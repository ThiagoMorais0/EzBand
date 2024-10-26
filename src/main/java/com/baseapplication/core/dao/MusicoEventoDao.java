package com.baseapplication.core.dao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.MusicoEventoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MusicoEventoDao extends JpaRepository<MusicoEvento, MusicoEventoId> {
    @Modifying
    @Query("DELETE FROM MusicoEvento me WHERE me.id.idEvento = :idEvento AND me.tipoEvento = :tipoEvento")
    void removerTodosParticipantes(Long id, TipoEvento tipoEvento);
}
