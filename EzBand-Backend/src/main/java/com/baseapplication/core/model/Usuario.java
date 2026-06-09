package com.baseapplication.core.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.baseapplication.core.model.publicacao.PublicacaoUsuario;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.enums.PermissaoUsuario;
import com.baseapplication.core.utils.DateUtils;

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
    private LocalDateTime dataUltimoLogin;
    private Boolean celularValidado = false;

    @Column(name = "end_pais")
    private String endPais;
    @Column(name = "end_estado")
    private String endEstado;
    @Column(name = "end_bairro")
    private String endBairro;
    @Column(name = "end_rua")
    private String endRua;
    @Column(name = "end_numero")
    private String endNumero;
    @Column(name = "end_cep")
    private String endCep;
    @Column(name = "end_complemento")
    private String endComplemento;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_tipos", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "tipo")
    private List<String> tiposUsuario = new ArrayList<>();

    @OneToMany(mappedBy = "id.idUsuario", fetch = FetchType.EAGER)
    private List<MusicoBanda> musicoBandaList = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PublicacaoUsuario> publicacoes = new ArrayList<>();

    public Usuario(CadastroDTO data) {
        this.nome = data.getNome();
        this.email = data.getEmail();
        this.senha = data.getSenha();
        this.permissao = PermissaoUsuario.USUARIO;
        this.ativo = true;
        this.bloqueado = false;
        this.urlFotoPerfil = "default";
        this.dataCriacao = LocalDate.now();
        this.dataNascimento = data.getDataNascimento();
        this.dataUltimoLogin = LocalDateTime.now();
    }

    public List<Banda> getBandas(){
        return musicoBandaList.stream().map(MusicoBanda::getBanda).collect(Collectors.toList());
    }

    public Usuario(CadastroUsuarioDTO usuarioDTO){
        this.nome = usuarioDTO.getNome();
        this.email = usuarioDTO.getEmail();
        this.senha = usuarioDTO.getSenha();
        this.permissao = PermissaoUsuario.USUARIO;
        this.cidade = usuarioDTO.getCidade();
        this.celular = usuarioDTO.getCelular();
        this.dataNascimento = DateUtils.stringToLocalDate(usuarioDTO.getNascimento());
        this.descricao = usuarioDTO.getBio() != null ? usuarioDTO.getBio() : "";
        this.endPais = usuarioDTO.getPais();
        this.endEstado = usuarioDTO.getEstado();
        this.endBairro = usuarioDTO.getBairro();
        this.endRua = usuarioDTO.getRua();
        this.endNumero = usuarioDTO.getNumero();
        this.endCep = usuarioDTO.getCep();
        this.endComplemento = usuarioDTO.getComplemento();
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
