package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoPeriodo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoDTO {
    private TipoPeriodo tipoPeriodo; // DIA, SEMANA, MES, ANO, TOTAL
    private Integer ano;
    private Integer mes; // 1-12
    private Integer dia; // 1-31
    private Integer semana; // 1-53
}
