package com.baseapplication.core.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.model.Ensaio;

@Repository
public interface EnsaioDao extends JpaRepository<Ensaio, Long> {
    @Query("SELECT DISTINCT e FROM Ensaio e INNER JOIN MusicoEvento me ON me.id.idEvento = e.id WHERE me.usuario.id = :idUsuario AND me.id.tipoEvento = com.baseapplication.core.enums.TipoEvento.ENSAIO")
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

    @Query("SELECT e FROM Ensaio e LEFT JOIN FETCH e.estudio " +
            "WHERE e.banda.id = :idBanda AND e.data IS NOT NULL AND e.data >= CURRENT_DATE " +
            "AND e.status IN ('PENDENTE', 'AGUARDANDO_APROVACAO') " +
            "ORDER BY e.data, e.horarioInicio")
    List<Ensaio> buscarFuturosNaoRealizadosPorBanda(@Param("idBanda") Long idBanda);

    @Query("SELECT e FROM Ensaio e LEFT JOIN FETCH e.estudio " +
            "INNER JOIN MusicoEvento me ON me.id.idEvento = e.id " +
            "AND me.id.tipoEvento = com.baseapplication.core.enums.TipoEvento.ENSAIO " +
            "WHERE e.banda.id = :idBanda AND me.usuario.id = :idUsuario " +
            "AND e.data IS NOT NULL AND e.data >= CURRENT_DATE " +
            "AND e.status NOT IN (com.baseapplication.core.enums.StatusEvento.REALIZADO, " +
            "com.baseapplication.core.enums.StatusEvento.CANCELADO) " +
            "ORDER BY e.data, e.horarioInicio")
    List<Ensaio> buscarFuturosPorBandaEMusico(@Param("idBanda") Long idBanda, @Param("idUsuario") Long idUsuario);

    @Query("SELECT e FROM Ensaio e INNER JOIN MusicoEvento me ON me.id.idEvento = e.id AND e.tipoEvento = 'ENSAIO' " +
            "WHERE me.usuario.id = :idUsuario AND e.data BETWEEN :inicio AND :fim " +
            "AND e.status IN ('PENDENTE', 'AGUARDANDO_APROVACAO') ORDER BY e.data, e.horarioInicio")
    List<Ensaio> buscarPorUsuarioEPeriodo(Long idUsuario, LocalDate inicio, LocalDate fim);

    @Query("SELECT e FROM Ensaio e INNER JOIN MusicoEvento me ON me.id.idEvento = e.id AND e.tipoEvento = 'ENSAIO' " +
            "WHERE me.usuario.id = :idUsuario AND e.data = :data AND e.status IN ('PENDENTE', 'AGUARDANDO_APROVACAO')")
    List<Ensaio> buscarPorUsuarioEData(Long idUsuario, LocalDate data);

    /**
     * Varredura do job que fecha eventos vencidos. CANCELADO fica de fora: evento
     * cancelado cuja data passou continua cancelado — sem esta exclusão o job o
     * marcaria como REALIZADO na rodada seguinte, desfazendo o cancelamento.
     */
    @Query("SELECT e FROM Ensaio e WHERE e.data <= :now "
            + "AND e.status NOT IN (com.baseapplication.core.enums.StatusEvento.REALIZADO, "
            + "com.baseapplication.core.enums.StatusEvento.CANCELADO)")
    List<Ensaio> buscarComDataAnteriorAHoje(LocalDate now);

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
