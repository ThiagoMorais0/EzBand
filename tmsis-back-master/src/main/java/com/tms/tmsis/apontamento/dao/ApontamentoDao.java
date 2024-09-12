package com.tms.tmsis.apontamento.dao;

import com.tms.tmsis.apontamento.enums.TipoApontamento;
import com.tms.tmsis.apontamento.model.Apontamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ApontamentoDao extends JpaRepository<Apontamento, Long> {

    @Query("SELECT a.tipo FROM Apontamento a where extract(month from a.dataApontamento) = extract(month from current_date)ORDER BY a.id DESC LIMIT 1")
    TipoApontamento buscarInicioOuTermino();
}
