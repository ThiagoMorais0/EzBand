package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class QuantidadeParticipacoesEspeciaisDTO {
    private Long qtdShows;
    private Long qtdEnsaios;
    /** Preenchido a parte: o construtor e usado por uma query JPQL de contagem. */
    private List<BandaParticipacaoResumoDTO> bandas = new ArrayList<>();

    public QuantidadeParticipacoesEspeciaisDTO(Long qtdShows, Long qtdEnsaios) {
        this.qtdShows = qtdShows;
        this.qtdEnsaios = qtdEnsaios;
    }
}
