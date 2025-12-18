package com.baseapplication.core.dto.metricas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetricaBandaMaisShowsDTO {
    private Long idBanda;
    private String nomeBanda;
    private Long quantidadeShows;
}
