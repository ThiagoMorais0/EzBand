package com.baseapplication.core.dao;

import com.baseapplication.core.dto.metricas.MetricaShowsPorCidadeDTO;
import com.baseapplication.core.dto.metricas.MetricaShowsPorEstadoDTO;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.superClasses.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowDao extends JpaRepository<Show, Long> {
    @Query("SELECT DISTINCT s FROM Show s INNER JOIN MusicoEvento me ON me.id.idEvento = s.id WHERE me.usuario.id = :idUsuario AND me.id.tipoEvento = com.baseapplication.core.enums.TipoEvento.SHOW")
    List<Show> buscarPorIdUsuario(Long idUsuario);

    @Query(value = "select distinct s.* " +
            "from show s  " +
            "left join musico_evento me on me.id_evento = s.id and me.tipo_evento = 'SHOW' " +
            "left join musico_banda mb on mb.id_banda = s.id_banda " +
            "where me.tipo_evento = 'SHOW' " +
            "  and s.id_banda = :idBanda " +
            "  and s.\"data\" >= CURRENT_DATE " +
            "  and s.\"data\" IS NOT NULL " +
            "  and s.status = :status " +
            "  and ( " +
            "      mb.permissao in ('ADMINISTRADOR', 'FUNDADOR')  " +
            "      or me.id_usuario = :idUsuario) " +
            "order by s.\"data\", s.horario_inicio", nativeQuery = true)
    List<Show> buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario, String status);

    @Query(value = "SELECT s FROM Show s " +
            "INNER JOIN MusicoEvento me ON me.id.idEvento = s.id AND s.tipoEvento = 'SHOW' " +
            "WHERE s.data = :data AND me.usuario.id = :idUsuario and s.status in ('PENDENTE', 'AGUARDANDO_APROVACAO') " +
            "ORDER BY s.data DESC LIMIT 1")
    Evento buscarPrimeiroPorUsuarioEData(Long idUsuario, LocalDate data);

    @Query(value = "SELECT s FROM Show s " +
            "INNER JOIN MusicoEvento me ON me.id.idEvento = s.id AND s.tipoEvento = 'SHOW' " +
            "WHERE s.data = :data AND me.usuario.id = :idUsuario and s.status in ('PENDENTE', 'AGUARDANDO_APROVACAO') " +
            "ORDER BY s.data DESC LIMIT 1")
    List<Evento> buscarPorUsuarioEData(Long idUsuario, LocalDate data);

    @Query("SELECT s FROM Show s INNER JOIN MusicoEvento me ON me.id.idEvento = s.id AND s.tipoEvento = 'SHOW' " +
            "WHERE me.usuario.id = :idUsuario AND s.data BETWEEN :inicio AND :fim " +
            "AND s.status IN ('PENDENTE', 'AGUARDANDO_APROVACAO') ORDER BY s.data, s.horarioInicio")
    List<Show> buscarPorUsuarioEPeriodo(Long idUsuario, LocalDate inicio, LocalDate fim);

    @Query(value = "SELECT e FROM Show e WHERE e.data <= :now and e.status <> :status ")
    List<Show> buscarComDataAnteriorAHoje(LocalDate now, StatusEvento status);

    // Métricas para Banda
    @Query("SELECT COUNT(s) FROM Show s WHERE s.banda.id = :idBanda AND s.status = 'REALIZADO' " +
            "AND s.data BETWEEN :dataInicio AND :dataFim")
    Long contarShowsRealizadosPorPeriodo(@Param("idBanda") Long idBanda, 
                                          @Param("dataInicio") LocalDate dataInicio, 
                                          @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COALESCE(SUM(s.valorContrato), 0) FROM Show s WHERE s.banda.id = :idBanda AND s.status = 'REALIZADO' " +
            "AND s.data BETWEEN :dataInicio AND :dataFim")
    BigDecimal somarValorFaturadoPorPeriodo(@Param("idBanda") Long idBanda, 
                                             @Param("dataInicio") LocalDate dataInicio, 
                                             @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COALESCE(AVG(s.valorContrato), 0) FROM Show s WHERE s.banda.id = :idBanda AND s.status = 'REALIZADO' " +
            "AND s.data BETWEEN :dataInicio AND :dataFim")
    BigDecimal calcularValorMedioPorPeriodo(@Param("idBanda") Long idBanda, 
                                             @Param("dataInicio") LocalDate dataInicio, 
                                             @Param("dataFim") LocalDate dataFim);

    @Query("SELECT new com.baseapplication.core.dto.metricas.MetricaShowsPorCidadeDTO(s.endereco.cidade, s.endereco.estado, COUNT(s)) " +
            "FROM Show s WHERE s.banda.id = :idBanda AND s.status = 'REALIZADO' " +
            "AND s.data BETWEEN :dataInicio AND :dataFim " +
            "GROUP BY s.endereco.cidade, s.endereco.estado " +
            "ORDER BY COUNT(s) DESC")
    List<MetricaShowsPorCidadeDTO> contarShowsPorCidade(@Param("idBanda") Long idBanda, 
                                                          @Param("dataInicio") LocalDate dataInicio, 
                                                          @Param("dataFim") LocalDate dataFim);

    @Query("SELECT new com.baseapplication.core.dto.metricas.MetricaShowsPorEstadoDTO(s.endereco.estado, COUNT(s)) " +
            "FROM Show s WHERE s.banda.id = :idBanda AND s.status = 'REALIZADO' " +
            "AND s.data BETWEEN :dataInicio AND :dataFim " +
            "GROUP BY s.endereco.estado " +
            "ORDER BY COUNT(s) DESC")
    List<MetricaShowsPorEstadoDTO> contarShowsPorEstado(@Param("idBanda") Long idBanda, 
                                                          @Param("dataInicio") LocalDate dataInicio, 
                                                          @Param("dataFim") LocalDate dataFim);

    @Query("SELECT new com.baseapplication.core.dto.metricas.MetricaShowsPorCidadeDTO(s.endereco.cidade, s.endereco.estado, COUNT(s)) " +
            "FROM Show s WHERE s.banda.id = :idBanda AND s.status = 'REALIZADO' " +
            "GROUP BY s.endereco.cidade, s.endereco.estado " +
            "ORDER BY COUNT(s) DESC")
    List<MetricaShowsPorCidadeDTO> buscarCidadeMaisTocada(@Param("idBanda") Long idBanda);

    // Métricas para Usuário
    @Query("SELECT COUNT(DISTINCT s) FROM Show s " +
            "INNER JOIN MusicoEvento me ON me.evento.id = s.id " +
            "WHERE me.usuario.id = :idUsuario AND s.status = 'REALIZADO' " +
            "AND s.data BETWEEN :dataInicio AND :dataFim")
    Long contarShowsUsuarioPorPeriodo(@Param("idUsuario") Long idUsuario, 
                                       @Param("dataInicio") LocalDate dataInicio, 
                                       @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COALESCE(SUM(me.cache), 0) FROM MusicoEvento me " +
            "INNER JOIN Show s ON s.id = me.evento.id " +
            "WHERE me.usuario.id = :idUsuario AND s.status = 'REALIZADO' " +
            "AND s.data BETWEEN :dataInicio AND :dataFim")
    BigDecimal somarCachesUsuarioPorPeriodo(@Param("idUsuario") Long idUsuario, 
                                             @Param("dataInicio") LocalDate dataInicio, 
                                             @Param("dataFim") LocalDate dataFim);

    @Query("SELECT COALESCE(AVG(me.cache), 0) FROM MusicoEvento me " +
            "INNER JOIN Show s ON s.id = me.evento.id " +
            "WHERE me.usuario.id = :idUsuario AND s.status = 'REALIZADO' " +
            "AND s.data BETWEEN :dataInicio AND :dataFim")
    BigDecimal calcularCacheMedioPorPeriodo(@Param("idUsuario") Long idUsuario, 
                                             @Param("dataInicio") LocalDate dataInicio, 
                                             @Param("dataFim") LocalDate dataFim);

    @Query("SELECT s.banda.id, s.banda.nome, COALESCE(SUM(me.cache), 0) " +
            "FROM MusicoEvento me " +
            "INNER JOIN Show s ON s.id = me.evento.id " +
            "WHERE me.usuario.id = :idUsuario AND s.status = 'REALIZADO' " +
            "GROUP BY s.banda.id, s.banda.nome " +
            "ORDER BY SUM(me.cache) DESC")
    List<Object[]> buscarBandaMaisRentavelParaUsuario(@Param("idUsuario") Long idUsuario);

    @Query("SELECT s.banda.id, s.banda.nome, COUNT(DISTINCT s) " +
            "FROM Show s " +
            "INNER JOIN MusicoEvento me ON me.evento.id = s.id " +
            "WHERE me.usuario.id = :idUsuario AND s.status = 'REALIZADO' " +
            "GROUP BY s.banda.id, s.banda.nome " +
            "ORDER BY COUNT(DISTINCT s) DESC")
    List<Object[]> buscarBandaMaisShowsParaUsuario(@Param("idUsuario") Long idUsuario);
}
