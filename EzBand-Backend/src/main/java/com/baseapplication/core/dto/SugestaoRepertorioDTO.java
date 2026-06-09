package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SugestaoRepertorioDTO {
    private Long idBanda;
    private Long idEvento;
    private Integer duracaoShowMinutos;
    private List<Integer> curvaEnergia;
}
