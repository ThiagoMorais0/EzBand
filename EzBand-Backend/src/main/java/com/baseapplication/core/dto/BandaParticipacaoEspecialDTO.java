package com.baseapplication.core.dto;

import com.baseapplication.core.model.Banda;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

public class BandaParticipacaoEspecialDTO {
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


    public BandaParticipacaoEspecialDTO(Banda banda){
        BeanUtils.copyProperties(banda, this);
        this.permiteEntradaPorConvite = banda.getParametros().getPermiteEntradaPorConvite();
        this.exigirAprovacaoCompromissos = banda.getParametros().getExigirAprovacaoCompromissos();
        this.listarObservacaoRepertorio = banda.getParametros().getListarObservacaoRepertorio();
        this.quantidadeShows = banda.getShows().size();
        this.quantidadeEnsaios = banda.getEnsaios().size();
        this.quantidadeNotificacoes = banda.getNotificacaoShows().size() + banda.getNotificacaoEnsaios().size();
    }
}
