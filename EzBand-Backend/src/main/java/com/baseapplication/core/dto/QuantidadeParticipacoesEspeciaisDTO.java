package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class QuantidadeParticipacoesEspeciaisDTO {
    private Long qtdShows;
    private Long qtdEnsaios;

    public QuantidadeParticipacoesEspeciaisDTO(Long qtdShows, Long qtdEnsaios) {
        this.qtdShows = qtdShows;
        this.qtdEnsaios = qtdEnsaios;
    }
}
