package com.baseapplication.core.model.dto;

import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.model.Banda;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class BandaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String categoria;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "Brazil/East")
    private LocalDate dataInclusao;
    private String urlLogo;
    private Boolean permiteEntradaPorConvite;
    private Boolean exigirAprovacaoCompromissos;
    private Boolean listarObservacaoRepertorio;
    private Integer quantidadeShows;
    private Integer quantidadeEnsaios;
    private Integer quantidadeNotificacoes;
    private Integer quantidadeMembros;
    private List<InfoPerfilUsuarioDTO> membros;


    public BandaDTO(Banda banda){
        BeanUtils.copyProperties(banda, this);
        this.permiteEntradaPorConvite = banda.getParametros().getPermiteEntradaPorConvite();
        this.exigirAprovacaoCompromissos = banda.getParametros().getExigirAprovacaoCompromissos();
        this.listarObservacaoRepertorio = banda.getParametros().getListarObservacaoRepertorio();
        this.quantidadeShows = banda.getShows().size();
        this.quantidadeEnsaios = banda.getEnsaios().size();
        this.quantidadeNotificacoes = banda.getNotificacaoShows().size() + banda.getNotificacaoEnsaios().size();
        this.quantidadeMembros = banda.getMusicos().size();
        this.membros = banda.getUsuariosMusicos().stream().map(InfoPerfilUsuarioDTO::new).collect(Collectors.toList());
    }
}
