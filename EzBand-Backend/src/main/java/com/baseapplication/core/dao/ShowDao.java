package com.baseapplication.core.dao;

import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.superClasses.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowDao extends JpaRepository<Show, Long> {
    @Query(value = "select s.* from musico_evento me " +
            "inner join show s on me.id_evento = s.id " +
            "where me.tipo_evento = 'SHOW' and me.id_usuario = :idUsuario", nativeQuery = true )
    List<Show> buscarPorIdUsuario(Long idUsuario);

    @Query(value = "select distinct s.* " +
            "from show s  " +
            "left join musico_evento me on me.id_evento = s.id and me.tipo_evento = 'SHOW' " +
            "left join musico_banda mb on mb.id_banda = s.id_banda " +
            "where me.tipo_evento = 'SHOW' " +
            "  and s.id_banda = :idBanda " +
            "  and s.\"data\" >= CURRENT_DATE " +
            "  and s.status = :status " +
            "  and ( " +
            "      mb.permissao in ('ADMINISTRADOR', 'FUNDADOR')  " +
            "      or me.id_usuario = :idUsuario)", nativeQuery = true)
    List<Show> buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario, String status);

    @Query(value = "SELECT s FROM Show s " +
            "INNER JOIN MusicoEvento me ON me.id.idEvento = s.id AND s.tipoEvento = 'SHOW' " +
            "WHERE s.data = :data AND me.usuario.id = :idUsuario " +
            "ORDER BY s.data DESC LIMIT 1")
    Evento buscarPrimeiroPorUsuarioEData(Long idUsuario, LocalDate data);
}
