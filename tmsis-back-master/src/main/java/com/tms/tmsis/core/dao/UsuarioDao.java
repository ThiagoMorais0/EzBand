package com.tms.tmsis.core.dao;


import com.tms.tmsis.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {

    UserDetails findByLogin(String login);

    @Query(value = "SELECT * FROM Usuario u WHERE u.login = :login", nativeQuery = true)
    Usuario findByUsername(String login);

    @Query(value = "SELECT * FROM Usuario u WHERE u.email = :email", nativeQuery = true)
    Usuario findByEmail(String email);
}
