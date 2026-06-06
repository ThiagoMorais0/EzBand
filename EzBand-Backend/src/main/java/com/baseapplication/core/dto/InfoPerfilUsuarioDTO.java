package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoRelacionamento;
import com.baseapplication.core.model.MembroFantasma;
import com.baseapplication.core.model.dto.BandaDTO;
import org.springframework.beans.BeanUtils;

import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.utils.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    private TipoRelacionamento tipoRelacionamento;
    private Long quantidadeSeguidores;
    private Long quantidadeSeguindo;
    private List<BandaDTO> bandas = new ArrayList<>();
    private List<PublicacaoDTO> publicacoes = new ArrayList<>();
    private String endPais;
    private String endEstado;
    private String endBairro;
    private String endRua;
    private String endNumero;
    private String endCep;
    private String endComplemento;
    public InfoPerfilUsuarioDTO(Usuario usuario){
        BeanUtils.copyProperties(usuario, this);

        if(usuario.getDataNascimento() != null)
            this.nascimento = DateUtils.localDateToString(usuario.getDataNascimento());

        if(usuario.getDataCriacao() != null)
            this.dataCriacao = DateUtils.localDateToString(usuario.getDataCriacao());


        this.setBandas(usuario.getBandas().stream().map(BandaDTO::new).toList());
        this.setPublicacoes(usuario.getPublicacoes().stream().map(PublicacaoDTO::new).sorted(Comparator.comparing(PublicacaoDTO::getDataPublicacao).reversed()).toList());
    }

    public InfoPerfilUsuarioDTO(MusicoBanda musico){
        BeanUtils.copyProperties(musico.getUsuario(), this);
        this.nascimento = DateUtils.localDateToString(musico.getUsuario().getDataNascimento());
        this.dataCriacao = DateUtils.localDateToString(musico.getUsuario().getDataCriacao());
        this.instrumentos = musico.getInstrumentos();
    }

    public InfoPerfilUsuarioDTO(MembroFantasma membroFantasma){
        BeanUtils.copyProperties(membroFantasma, this);
        this.urlFotoPerfil = membroFantasma.getUrlFoto();
        this.instrumentos = membroFantasma.getInstrumento();
    }
}
