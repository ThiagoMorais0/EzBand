package com.baseapplication.core.dao;

import com.baseapplication.core.dto.BuscaGlobalProjection;
import com.baseapplication.core.dto.BandaParticipacaoResumoDTO;
import com.baseapplication.core.dto.QuantidadeParticipacoesEspeciaisDTO;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.publicacoes WHERE u.id = :id")
    Optional<Usuario> findByIdWithPublicacoes(Long id);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.publicacoes WHERE u.email = :email")
    Optional<Usuario> findByEmailWithPublicacoes(String email);

//    UserDetails findByLogin(String login);

//    @Query(value = "SELECT u FROM Usuario u WHERE u.login = :login")
//    Usuario findByUsername(String login);

    @Query(value = "SELECT u FROM Usuario u WHERE u.email = :email")
    Usuario findByEmail(String email);
    @Query(value = "SELECT u FROM Usuario u WHERE u.celular = :celular")
    Usuario findByCelular(String celular);

    @Query(value = "SELECT COUNT(1)  " +
            "FROM musico_evento me " +
            "LEFT JOIN show s ON me.id_evento = s.id " +
            "LEFT JOIN ensaio e ON me.id_evento = e.id " +
            "LEFT JOIN evento ev ON me.id_evento = ev.id " +
            "WHERE me.id_usuario = :idUsuario  " +
            "  AND ( " +
            "    (s.id IS NOT NULL AND ev.status = 'PENDENTE')  " +
            "    OR  " +
            "    (e.id IS NOT NULL AND ev.status = 'PENDENTE'))", nativeQuery = true)
    Integer buscarQuantidadeProximosEventos(Long idUsuario);

    @Query("""
    SELECT new com.baseapplication.core.dto.QuantidadeParticipacoesEspeciaisDTO(
        COUNT(DISTINCT CASE WHEN ev.tipoEvento = 'SHOW' THEN me.id.idEvento END),
        COUNT(DISTINCT CASE WHEN ev.tipoEvento = 'ENSAIO' THEN me.id.idEvento END)
    )
    FROM MusicoEvento me
    JOIN me.evento ev
    WHERE me.id.idUsuario = :idUsuario
      AND ev.status = 'PENDENTE'
      AND NOT EXISTS (
          SELECT 1
          FROM MusicoBanda mb
          WHERE mb.banda.id = ev.banda.id
            AND mb.usuario.id = me.id.idUsuario
      )
""")
    QuantidadeParticipacoesEspeciaisDTO buscarQuantidadeParticipacoesEspeciais(Long idUsuario);

    @Query("""
    SELECT new com.baseapplication.core.dto.BandaParticipacaoResumoDTO(
        b.id, b.nome, b.urlLogo, COUNT(me.id.idEvento)
    )
    FROM MusicoEvento me
    JOIN me.evento ev
    JOIN ev.banda b
    WHERE me.id.idUsuario = :idUsuario
      AND ev.status = 'PENDENTE'
      AND NOT EXISTS (
          SELECT 1
          FROM MusicoBanda mb
          WHERE mb.banda.id = b.id
            AND mb.usuario.id = me.id.idUsuario
      )
    GROUP BY b.id, b.nome, b.urlLogo
    ORDER BY COUNT(me.id.idEvento) DESC, b.nome
""")
    List<BandaParticipacaoResumoDTO> buscarBandasParticipacoesEspeciais(Long idUsuario);

    /**
     * Shows de outras bandas em que o usuario participa, em qualquer status, para a vitrine
     * publica (perfil e agenda). So entra participacao aceita: convite pendente, recusado ou
     * inativo nao e publico. Situacao nula sao registros antigos, anteriores ao convite.
     */
    @Query("""
    SELECT s
    FROM MusicoEvento me
    JOIN Show s ON s.id = me.id.idEvento
    WHERE me.id.idUsuario = :idUsuario
      AND me.id.tipoEvento = com.baseapplication.core.enums.TipoEvento.SHOW
      AND s.data IS NOT NULL
      AND (me.situacao IS NULL OR me.situacao = com.baseapplication.core.enums.SituacaoMusicoEvento.ATIVO)
      AND NOT EXISTS (
          SELECT 1
          FROM MusicoBanda mb
          WHERE mb.banda.id = s.banda.id
            AND mb.usuario.id = me.id.idUsuario
      )
    ORDER BY s.data, s.horarioInicio
""")
    List<Show> buscarShowsParticipacoesEspeciaisPublicos(Long idUsuario);

    @Query("""
    SELECT new com.baseapplication.core.model.dto.ShowDTO(s, me.cache)
    FROM MusicoEvento me
    JOIN Show s ON s.id = me.id.idEvento
    WHERE me.id.idUsuario = :idUsuario
      AND s.tipoEvento = 'SHOW'
      AND s.status = 'PENDENTE'
      AND NOT EXISTS (
          SELECT 1
          FROM MusicoBanda mb
          WHERE mb.banda.id = s.banda.id
            AND mb.usuario.id = me.id.idUsuario
      )
""")
    List<ShowDTO> buscarShowsEspeciais(Long idUsuario);

    @Query("""
    SELECT new com.baseapplication.core.model.dto.EnsaioDTO(e)
    FROM MusicoEvento me
    JOIN Ensaio e ON e.id = me.id.idEvento
    WHERE me.id.idUsuario = :idUsuario
      AND e.tipoEvento = 'ENSAIO'
      AND e.status = 'PENDENTE'
      AND NOT EXISTS (
          SELECT 1
          FROM MusicoBanda mb
          WHERE mb.banda.id = e.banda.id
            AND mb.usuario.id = me.id.idUsuario
      )
""")
    List<EnsaioDTO> buscarEnsaiosEspeciais(Long idUsuario);

    @Query(value = """
            SELECT id, nome, 'USUARIO' as tipo, url_foto_perfil as urlFoto FROM usuario WHERE nome ILIKE %:termo%
            UNION ALL
            SELECT id, nome, 'BANDA' as tipo, url_logo as urlFoto FROM banda WHERE nome ILIKE %:termo%
            UNION ALL
            SELECT id, nome, 'ESTUDIO' as tipo, url_foto_perfil as urlFoto FROM estudio WHERE nome ILIKE %:termo%
            UNION ALL
            SELECT id, nome, 'LOCAL_EVENTO' as tipo, url_foto_perfil as urlFoto FROM local_evento WHERE nome ILIKE %:termo%
            LIMIT 50
            """, nativeQuery = true)
    List<BuscaGlobalProjection> buscarGlobal(String termo);

    @Query("SELECT u FROM Usuario u " +
            "WHERE (:#{#termo} IS NULL OR :#{#termo} = '' OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :#{#termo}, '%')))")
    List<Usuario> buscarSugestoes(String termo);
}
