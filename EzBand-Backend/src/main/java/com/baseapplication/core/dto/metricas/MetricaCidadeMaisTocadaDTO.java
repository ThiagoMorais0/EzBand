package com.baseapplication.core.dto.metricas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MetricaCidadeMaisTocadaDTO {
    private String cidade;
    private String estado;
    private Long quantidade;
}
