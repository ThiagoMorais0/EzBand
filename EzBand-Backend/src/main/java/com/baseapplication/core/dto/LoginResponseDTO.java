package com.baseapplication.core.dto;

import com.baseapplication.core.model.Usuario;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LoginResponseDTO {
    private String nome;
    private Long idUsuario;
    private String urlFotoPerfil;
    private String celular;
    private Boolean celularValidado;
    private List<String> tiposUsuario;
    private Boolean cadastroCompleto;

    public LoginResponseDTO(Usuario usuario) {
        this.nome = usuario.getNome();
        this.idUsuario = usuario.getId();
        this.urlFotoPerfil = usuario.getUrlFotoPerfil();
        this.celular = usuario.getCelular();
        this.celularValidado = Boolean.TRUE.equals(usuario.getCelularValidado());
        this.tiposUsuario = usuario.getTiposUsuario() != null ? usuario.getTiposUsuario() : new ArrayList<>();
        this.cadastroCompleto = !Boolean.FALSE.equals(usuario.getCadastroCompleto());
    }
}
