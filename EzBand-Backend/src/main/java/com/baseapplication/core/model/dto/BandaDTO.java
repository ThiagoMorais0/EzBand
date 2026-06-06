package com.baseapplication.core.model.dto;

import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.PublicacaoDTO;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.StatusNotificacao;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.utils.Context;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class BandaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String categoria;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "Brazil/East")
    private LocalDate dataInclusao;
    private String urlLogo;
    private String urlBanner;
    private String instagramUrl;
    private String facebookUrl;
    private String youtubeUrl;
    private Boolean permiteEntradaPorConvite;
    private Boolean exigirAprovacaoCompromissos;
    private Boolean listarObservacaoRepertorio;
    private Integer quantidadeShows;
    private Integer quantidadeEnsaios;
    private Integer quantidadeNotificacoes;
    private Integer quantidadeMembros;
    private List<InfoPerfilUsuarioDTO> membros;
    private List<PublicacaoDTO> publicacoes;


    public BandaDTO(Banda banda){
        BeanUtils.copyProperties(banda, this);
        this.permiteEntradaPorConvite = banda.getParametros().getPermiteEntradaPorConvite();
        this.exigirAprovacaoCompromissos = banda.getParametros().getExigirAprovacaoCompromissos();
        this.listarObservacaoRepertorio = banda.getParametros().getListarObservacaoRepertorio();
        this.quantidadeShows = banda.getShows().stream().filter(i -> i.getStatus().equals(StatusEvento.PENDENTE)).toList().size();
        this.quantidadeEnsaios = banda.getEnsaios().stream().filter(i -> i.getStatus().equals(StatusEvento.PENDENTE)).toList().size();
        this.quantidadeNotificacoes = 0;
//        this.quantidadeNotificacoes = (int) banda.getNotificacaoShows().stream()
//                        .filter(notificacao -> notificacao.getStatusNotificacao().equals(StatusNotificacao.NAO_VISUALIZADO))
//                        .filter(notificacao -> notificacao.getDestinatario().equals(Context.getUsuarioLogado()))
//                        .count() +
//                        (int) banda.getNotificacaoEnsaios().stream()
//                                .filter(notificacao -> notificacao.getStatusNotificacao().equals(StatusNotificacao.NAO_VISUALIZADO))
//                                .filter(notificacao -> notificacao.getDestinatario().equals(Context.getUsuarioLogado()))
//                                .count();

        this.quantidadeMembros = banda.getMusicos().size() + banda.getMembrosFantasma().size();
        //Em membros, somar membros normais com membros fantasma
        this.membros = banda.getMusicos().stream().map(InfoPerfilUsuarioDTO::new).collect(Collectors.toList());
        this.membros.addAll(banda.getMembrosFantasma().stream().map(InfoPerfilUsuarioDTO::new).collect(Collectors.toList()));
        //ORDENAR por data de publicação
        this.publicacoes = banda.getPublicacoes().stream().map(PublicacaoDTO::new)
                .sorted(Comparator.comparing(PublicacaoDTO::getDataPublicacao).reversed()).toList();
    }
}
