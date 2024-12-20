package com.baseapplication.core.dao;

import com.baseapplication.core.model.Banda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface BandaDao extends JpaRepository<Banda, Long> {

//    @Query(value = "SELECT b FROM banda b INNER JOIN musico_banda m ON m.id_banda = b.id WHERE m.id_usuario = :idUsuario", nativeQuery = true)
    @Query("SELECT b FROM Banda b INNER JOIN fetch MusicoBanda m ON m.id.idBanda = b.id WHERE m.id.idUsuario = :idUsuario")
    Optional<List<Banda>> buscarBandasPorUsuario(Long idUsuario);

    @Query(value = "SELECT DISTINCT b from Banda b " +
            "INNER JOIN Show s on s.banda.id = b.id " +
            "INNER JOIN MusicoEvento me on me.id.idEvento = s.id " +
            "WHERE me.id.idUsuario = :idUsuario " +
            "AND s.banda.id NOT IN (SELECT m.banda.id FROM MusicoBanda m where m.usuario.id = :idUsuario)")
    Optional<List<Banda>> buscarParticipacoesEspeciais(Long idUsuario);

    @Query(value = "select count(distinct s.id) " +
            "from show s " +
            "left join musico_evento me on me.id_evento = s.id and me.tipo_evento = 'SHOW' " +
            "left join musico_banda mb on mb.id_banda = s.id_banda " +
            "where me.tipo_evento = 'SHOW' " +
            "  and s.id_banda = :idBanda " +
            "  and ( " +
            "      mb.permissao in ('ADMINISTRADOR', 'FUNDADOR')  " +
            "      or me.id_usuario = :idUsuario " +
            "  )", nativeQuery = true)
    Integer buscarQuantidadeDeShows(Long idBanda, Long idUsuario);

    @Query(value = "select count(distinct e.id) " +
            "from ensaio e " +
            "left join musico_evento me on me.id_evento = e.id and me.tipo_evento = 'ENSAIO' " +
            "left join musico_banda mb on mb.id_banda = e.id_banda " +
            "where me.tipo_evento = 'ENSAIO' " +
            "  and e.id_banda = :idBanda " +
            "  and ( " +
            "      mb.permissao in ('ADMINISTRADOR', 'FUNDADOR')  " +
            "      or me.id_usuario = :idUsuario " +
            "  )", nativeQuery = true)
    Integer buscarQuantidadeDeEnsaios(Long idBanda, Long idUsuario);

    @Query(value = "SELECT COUNT(distinct n.id) AS total_notificacoes " +
            "FROM NOTIFICACAO n " +
            "JOIN ESTADO_NOTIFICACAO en ON en.id_notificacao = n.id " +
            "JOIN BANDA b ON b.id = n.id_banda_destino " +
            "JOIN musico_banda ub ON ub.id_banda = b.id " +
            "JOIN USUARIO u ON ub.id_usuario = u.id " +
            "WHERE ( " +
            "    (n.tipo_notificacao = 0 AND ub.permissao in ('ADMINISTRADOR', 'FUNDADOR')) " +
            "    OR " +
            "    (n.tipo_notificacao = 2) " +
            ") " +
            "AND en.status_notificacao = 'NAO_VISUALIZADO';", nativeQuery = true)
    Integer buscarQuantidadeDeNotificacoes(Long idBanda);

    @Query(value = "select count(*) from musico_banda mb where id_banda = :idBanda", nativeQuery = true)
    Integer buscarQuantidadeDeMembros(Long idBanda);

    @Query(value = "select count(distinct rb.id) from repertorio_banda rb where id_banda = :idBanda", nativeQuery = true)
    Integer buscarQuantidadeDeMusicasNoRepertorio(Long idBanda);
}
