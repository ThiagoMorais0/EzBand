package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoPendenciasAvaliacaoDTO {
    
    private Integer totalPendencias;
    private Integer pendenciasShows;
    private Integer pendenciasEnsaios;
    private List<PendenciaAvaliacaoEventoDTO> pendencias;
}
