package com.baseapplication.core.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.enums.PermissaoUsuario;
import com.baseapplication.core.utils.DateUtils;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private String email;
    private String celular;
    private String cidade;
    private String descricao;
    private String senha;
    private LocalDate dataNascimento;
    private LocalDate dataCriacao;
    private Boolean ativo;
    private Boolean bloqueado;
    @Enumerated(EnumType.STRING)
    private PermissaoUsuario permissao;
    private String urlFotoPerfil;
    @OneToMany(mappedBy = "id.idUsuario", fetch = FetchType.LAZY)
    private List<MusicoBanda> musicoBandaList;

    public Usuario(CadastroDTO data) {
        this.nome = data.getNome();
        this.email = data.getEmail();
        this.senha = data.getSenha();
        this.permissao = PermissaoUsuario.USUARIO;
        this.ativo = true;
        this.bloqueado = false;
        this.urlFotoPerfil = "default";
        this.dataCriacao = LocalDate.now();

    }

    public List<Banda> getBandas(){
        return musicoBandaList.stream().map(MusicoBanda::getBanda).collect(Collectors.toList());
    }

    public Usuario(CadastroUsuarioDTO usuarioDTO, String urlImagem){
        this.nome = usuarioDTO.getNome();
        this.email = usuarioDTO.getEmail();
        this.senha = usuarioDTO.getSenha();
        this.permissao = PermissaoUsuario.USUARIO;
        this.cidade = usuarioDTO.getCidade();
        this.celular = usuarioDTO.getCelular();
        this.dataNascimento = DateUtils.stringToLocalDate(usuarioDTO.getNascimento());
        this.ativo = true;
        this.bloqueado = false;
        this.urlFotoPerfil = urlImagem;
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
        return email;
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
