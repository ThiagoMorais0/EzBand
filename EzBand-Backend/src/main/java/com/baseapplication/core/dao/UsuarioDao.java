package com.baseapplication.core.dao;

import com.baseapplication.core.dto.BuscaGlobalProjection;
import com.baseapplication.core.dto.QuantidadeParticipacoesEspeciaisDTO;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.model.Usuario;

import java.util.List;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {

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
            "WHERE me.id_usuario = :idUsuario  " +
            "  AND ( " +
            "    (s.id IS NOT NULL AND s.status = 'PENDENTE')  " +
            "    OR  " +
            "    (e.id IS NOT NULL AND e.status = 'PENDENTE'))", nativeQuery = true)
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
