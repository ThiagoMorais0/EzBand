package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Banda em que o usuario tem participacao especial, com o total de eventos pendentes
 * nela -- o painel usa o total para dimensionar o logo no mosaico.
 */
@Getter
@Setter
@NoArgsConstructor
public class BandaParticipacaoResumoDTO {
    private Long id;
    private String nome;
    private String urlLogo;
    private Long qtdEventos;

    public BandaParticipacaoResumoDTO(Long id, String nome, String urlLogo, Long qtdEventos) {
        this.id = id;
        this.nome = nome;
        this.urlLogo = urlLogo;
        this.qtdEventos = qtdEventos;
    }
}
