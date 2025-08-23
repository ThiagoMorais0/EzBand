package com.baseapplication.core.service.impl;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.RepertorioEventoId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.RepertorioEventoId;

import jakarta.transaction.Transactional;

@Repository
public interface RepertorioEventoDao extends JpaRepository<RepertorioEvento, RepertorioEventoId> {

    @Transactional
    @Modifying
    @Query(value = "delete from repertorio_evento where id_evento = :idEvento and tipo_evento = :tipoEvento", nativeQuery = true)
    void limparRepertorioEvento(Long idEvento, String tipoEvento);
}
