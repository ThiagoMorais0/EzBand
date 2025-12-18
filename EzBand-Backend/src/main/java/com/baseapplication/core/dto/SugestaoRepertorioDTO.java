package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SugestaoRepertorioDTO {
    private Long idBanda;
    private Long idEvento;
    private Integer duracaoShowMinutos; // Duração total do show em minutos
}
