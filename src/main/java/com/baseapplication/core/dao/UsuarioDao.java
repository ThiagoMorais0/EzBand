package com.baseapplication.core.dao;


import com.baseapplication.core.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioDao extends JpaRepository<Usuario, Long> {

    UserDetails findByLogin(String login);

    @Query(value = "SELECT u FROM Usuario u WHERE u.login = :login")
    Usuario findByUsername(String login);

    @Query(value = "SELECT u FROM Usuario u WHERE u.email = :email")
    Usuario findByEmail(String email);
}
