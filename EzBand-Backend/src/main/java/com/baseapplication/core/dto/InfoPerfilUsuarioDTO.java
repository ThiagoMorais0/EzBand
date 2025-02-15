package com.baseapplication.core.dto;

import org.springframework.beans.BeanUtils;

import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.utils.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoPerfilUsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String celular;
    private String dataCriacao;
    private String nascimento;
    private String cidade;
    private String descricao;
    private String urlFotoPerfil;
    private String instrumentos;
    public InfoPerfilUsuarioDTO(Usuario usuario){
        BeanUtils.copyProperties(usuario, this);
        this.nascimento = DateUtils.localDateToString(usuario.getDataNascimento());
        this.dataCriacao = DateUtils.localDateToString(usuario.getDataCriacao());
    }

    public InfoPerfilUsuarioDTO(MusicoBanda musico){
        BeanUtils.copyProperties(musico.getUsuario(), this);
        this.nascimento = DateUtils.localDateToString(musico.getUsuario().getDataNascimento());
        this.dataCriacao = DateUtils.localDateToString(musico.getUsuario().getDataCriacao());
        this.instrumentos = musico.getInstrumentos();
    }
}
