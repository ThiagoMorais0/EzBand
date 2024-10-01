package com.baseapplication.core.model;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.enums.PermissaoUsuario;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private String nome;
    private String login;
    private String email;
    private String celular;
    private String senha;
    private LocalDate dataCriacao;
    private Boolean ativo;
    private Boolean bloqueado;
    private PermissaoUsuario permissao;
    private String urlFotoPerfil;
    @OneToMany(mappedBy = "id.idUsuario", fetch = FetchType.LAZY)
    private List<MusicoBanda> musicoBandaList;

    public List<Banda> getBandas(){
        return musicoBandaList.stream().map(MusicoBanda::getBanda).collect(Collectors.toList());
    }

    public Usuario(CadastroDTO data){
        this.login = data.getLogin();
        this.nome = data.getNome();
        this.email = data.getEmail();
        this.senha = data.getSenha();
        this.permissao = PermissaoUsuario.USUARIO;
        this.ativo = true;
        this.bloqueado = false;
        this.dataCriacao = LocalDate.now();
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
