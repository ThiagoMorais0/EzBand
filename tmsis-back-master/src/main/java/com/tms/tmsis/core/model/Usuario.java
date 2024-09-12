package com.tms.tmsis.core.model;

import com.tms.tmsis.core.dto.CadastroDTO;
import com.tms.tmsis.core.enums.PermissaoUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "LOGIN")
    private String login;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "SENHA")
    private String senha;

    @Column(name = "DATA_CRIACAO")
    private Date dataCriacao;

    @Column(name = "ATIVO")
    private Boolean ativo;

    @Column(name = "BLOQUEADO")
    private Boolean bloqueado;

    @Column(name = "PERMISSAO")
    private PermissaoUsuario permissao;

    public Usuario(CadastroDTO data){
        this.login = data.getLogin();
        this.nome = data.getNome();
        this.email = data.getEmail();
        this.senha = data.getSenha();
        this.permissao = PermissaoUsuario.USUARIO;
        this.ativo = true;
        this.bloqueado = false;
        this.dataCriacao = new Date();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.permissao == PermissaoUsuario.ADMIN)
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        else
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return nome;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo;
    }
}
