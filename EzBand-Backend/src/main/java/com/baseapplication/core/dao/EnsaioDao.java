package com.baseapplication.core.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.baseapplication.core.enums.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.model.Ensaio;

@Repository
public interface EnsaioDao extends JpaRepository<Ensaio, Long> {
    @Query(value = "select distinct e.* from musico_evento me " +
            "inner join ensaio e on me.id_evento = e.id " +
            "where me.tipo_evento = 'ENSAIO' and me.id_usuario = :idUsuario", nativeQuery = true )
    List<Ensaio> buscarPorIdUsuario(Long idUsuario);

    @Query(value = "select distinct e.* " +
            "from ensaio e  " +
            "left join musico_evento me on me.id_evento = e.id and me.tipo_evento = 'ENSAIO' " +
            "left join musico_banda mb on mb.id_banda = e.id_banda " +
            "where me.tipo_evento = 'ENSAIO' " +
            "  and e.id_banda = :idBanda " +
            "  and e.\"data\" >= CURRENT_DATE " +
            "  and e.\"data\" IS NOT NULL " +
            "  and e.status = :status " +
            "  and ( " +
            "      mb.permissao in ('ADMINISTRADOR', 'FUNDADOR')  " +
            "      or me.id_usuario = :idUsuario) " +
            "order by e.\"data\", e.horario_inicio", nativeQuery = true)
    List<Ensaio> buscarEnsaiosPorStatusBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario, String status);

    @Query(value = "SELECT e FROM Ensaio e WHERE e.data <= :now and e.status <> :status ")
    List<Ensaio> buscarComDataAnteriorAHoje(LocalDate now, StatusEvento status);

    // Métricas para Banda
    @Query("SELECT COALESCE(SUM(e.valor), 0) FROM Ensaio e WHERE e.banda.id = :idBanda AND e.status = 'REALIZADO' " +
            "AND e.data BETWEEN :dataInicio AND :dataFim")
    BigDecimal somarValorGastoPorPeriodo(@Param("idBanda") Long idBanda, 
                                          @Param("dataInicio") LocalDate dataInicio, 
                                          @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COALESCE(AVG(e.valor), 0) FROM Ensaio e WHERE e.banda.id = :idBanda AND e.status = 'REALIZADO' " +
            "AND e.data BETWEEN :dataInicio AND :dataFim")
    BigDecimal calcularValorMedioPorPeriodo(@Param("idBanda") Long idBanda, 
                                             @Param("dataInicio") LocalDate dataInicio, 
                                             @Param("dataFim") LocalDate dataFim);
}
