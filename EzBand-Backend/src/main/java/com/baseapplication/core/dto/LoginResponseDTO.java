package com.baseapplication.core.dto;

import com.baseapplication.core.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private String token;
    private String nome;
    private Long idUsuario;
    private String urlFotoPerfil;
    private String celular;
    private Boolean celularValidado;

    public LoginResponseDTO(String token, Usuario usuario){
        this.token = token;
        this.nome = usuario.getNome();
        this.idUsuario = usuario.getId();
        this.urlFotoPerfil = usuario.getUrlFotoPerfil();
        this.celular = usuario.getCelular();
        this.celularValidado = Boolean.TRUE.equals(usuario.getCelularValidado());
    }
}
