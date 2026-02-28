package com.baseapplication.core.dto;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.baseapplication.core.enums.PermissaoUsuario;
import com.baseapplication.core.model.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements UserDetails, Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String senha;
    private PermissaoUsuario permissao;
    private Boolean ativo;
    private Boolean bloqueado;

    public static UserPrincipal fromUsuario(Usuario usuario) {
        return new UserPrincipal(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getSenha(),
            usuario.getPermissao(),
            usuario.getAtivo(),
            usuario.getBloqueado()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.permissao == PermissaoUsuario.ADMIN) {
            return List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
            );
        } else {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return bloqueado == null || !bloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return ativo != null && ativo;
    }
}
