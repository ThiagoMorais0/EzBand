package com.baseapplication.core.dto;

import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CadastroUsuarioDTO {

    private String nome;
    private String login;
    private String senha;
    private String email;
    private String celular;
    private String cidade;
    private String nascimento;

    public Usuario toEntity(String url){
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setSenha(senha);
        usuario.setEmail(email);
        usuario.setCidade(cidade);
        usuario.setCelular(celular);
        usuario.setDataNascimento(DateUtils.stringToLocalDate(nascimento));
        usuario.setUrlFotoPerfil(url);
        usuario.setDescricao("");
//        usuario.setInstrumentos("");
        return usuario;
    }

//    public Usuario toEntity(){
//        Usuario usuario = new Usuario();
//        usuario.setNome(nome);
//        usuario.setSobrenome(sobrenome);
//        usuario.setSenha(senha);
//        usuario.setEmail(email);
//        usuario.setCidade(cidade);
//        usuario.setCelular(celular);
//        usuario.setDataNascimento(nascimento);
//        usuario.setUrlFotoPerfil("default");
//        usuario.setDescricao("");
//        usuario.setInstrumentos("");
//        return usuario;
//    }
}
