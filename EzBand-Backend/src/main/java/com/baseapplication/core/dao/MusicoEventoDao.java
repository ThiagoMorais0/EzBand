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
    @Query(value = "delete from musico_evento me where me.id_evento = :idEvento and me.tipo_evento = :tipoEvento ", nativeQuery = true)
    void removerTodosParticipantes(Long idEvento, TipoEvento tipoEvento);
}
