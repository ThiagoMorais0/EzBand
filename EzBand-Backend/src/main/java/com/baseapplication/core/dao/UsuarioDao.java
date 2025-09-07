package com.baseapplication.core.dao;


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
    SELECT new com.baseapplication.core.model.dto.ShowDTO(ev)
    FROM MusicoEvento me
    JOIN me.evento ev
    WHERE me.id.idUsuario = :idUsuario
      AND ev.tipoEvento = 'SHOW'
      AND ev.status = 'PENDENTE'
      AND NOT EXISTS (
          SELECT 1
          FROM MusicoBanda mb
          WHERE mb.banda.id = ev.banda.id
            AND mb.usuario.id = me.id.idUsuario
      )
""")
    List<ShowDTO> buscarShowsEspeciais(Long idUsuario);

    @Query("""
    SELECT new com.baseapplication.core.model.dto.EnsaioDTO(ev)
    FROM MusicoEvento me
    JOIN me.evento ev
    WHERE me.id.idUsuario = :idUsuario
      AND ev.tipoEvento = 'ENSAIO'
      AND ev.status = 'PENDENTE'
      AND NOT EXISTS (
          SELECT 1
          FROM MusicoBanda mb
          WHERE mb.banda.id = ev.banda.id
            AND mb.usuario.id = me.id.idUsuario
      )
""")
    List<EnsaioDTO> buscarEnsaiosEspeciais(Long idUsuario);
}
