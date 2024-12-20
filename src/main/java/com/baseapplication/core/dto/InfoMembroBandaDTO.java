package com.baseapplication.core.dto;

import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoMembroBandaDTO {
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
    private Integer permissao;

    public InfoMembroBandaDTO(Usuario usuario){
        BeanUtils.copyProperties(usuario, this);
        this.nascimento = DateUtils.localDateToString(usuario.getDataNascimento());
        this.dataCriacao = DateUtils.localDateToString(usuario.getDataCriacao());
    }

    public InfoMembroBandaDTO(MusicoBanda musico){
        BeanUtils.copyProperties(musico.getUsuario(), this);
        this.nascimento = DateUtils.localDateToString(musico.getUsuario().getDataNascimento());
        this.dataCriacao = DateUtils.localDateToString(musico.getUsuario().getDataCriacao());
        this.instrumentos = musico.getInstrumentos();
        this.permissao = musico.getPermissao().getNivel();
    }
}
