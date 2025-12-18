package com.baseapplication.core.dto;

import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.model.MembroFantasma;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

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
    private List<String> permissoes;
    private Boolean isMembroFantasma;

    public InfoMembroBandaDTO(Usuario usuario){
        BeanUtils.copyProperties(usuario, this);
        this.nascimento = DateUtils.localDateToString(usuario.getDataNascimento());
        this.dataCriacao = DateUtils.localDateToString(usuario.getDataCriacao());
        this.isMembroFantasma = false;
    }

    public InfoMembroBandaDTO(MusicoBanda musico){
        BeanUtils.copyProperties(musico.getUsuario(), this);
        this.nascimento = DateUtils.localDateToString(musico.getUsuario().getDataNascimento());
        this.dataCriacao = DateUtils.localDateToString(musico.getUsuario().getDataCriacao());
        this.instrumentos = musico.getInstrumentos();
        this.permissoes = musico.getPermissoes().stream().map(Enum::toString).toList();
        this.isMembroFantasma = false;
    }

    public InfoMembroBandaDTO(MembroFantasma membroFantasma){
        this.id = membroFantasma.getId();
        this.nome = membroFantasma.getNome();
        this.urlFotoPerfil = membroFantasma.getUrlFoto();
        this.instrumentos = membroFantasma.getInstrumento();
        this.descricao = membroFantasma.getObservacoes();
        this.isMembroFantasma = true;
        this.permissoes = new ArrayList<>();
    }
}
