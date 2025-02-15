package com.baseapplication.core.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.model.Usuario;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {

//    UserDetails findByLogin(String login);

//    @Query(value = "SELECT u FROM Usuario u WHERE u.login = :login")
//    Usuario findByUsername(String login);

    @Query(value = "SELECT u FROM Usuario u WHERE u.email = :email")
    Usuario findByEmail(String email);
    @Query(value = "SELECT u FROM Usuario u WHERE u.celular = :celular")
    Usuario findByCelular(String celular);

    @Query(value = "select count(1) " +
            "from notificacao n  " +
            "where n.id_destinatario = :idUsuario and n.status_notificacao in ('NAO_VISUALIZADO', 'VISUALIZADO')",
            nativeQuery = true)
    Integer buscarQuantidadeNotificacoes(Long idUsuario);

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

    @Query(value =
            "SELECT COUNT(1) " +
            "FROM musico_evento me " +
            "LEFT JOIN show s ON me.id_evento = s.id " +
            "LEFT JOIN ensaio e ON me.id_evento = e.id " +
            "LEFT JOIN banda b_s ON s.id_banda = b_s.id " +
            "LEFT JOIN banda b_e ON e.id_banda = b_e.id " +
            "LEFT JOIN musico_banda mb_s ON mb_s.id_banda = b_s.id AND mb_s.id_usuario = me.id_usuario " +
            "LEFT JOIN musico_banda mb_e ON mb_e.id_banda = b_e.id AND mb_e.id_usuario = me.id_usuario " +
            "WHERE me.id_usuario = :idUsuario  " +
            "  AND ( " +
            "    (s.id IS NOT NULL AND s.status = 'PENDENTE' AND mb_s.id_usuario IS NULL)  " +
            "    OR  " +
            "    (e.id IS NOT NULL AND e.status = 'PENDENTE' AND mb_e.id_usuario IS NULL))", nativeQuery = true)
    Integer buscarQuantidadeParticipacoesEspeciais(Long idUsuario);

}
