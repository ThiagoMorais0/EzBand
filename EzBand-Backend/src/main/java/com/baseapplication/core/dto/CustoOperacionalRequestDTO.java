package com.baseapplication.core.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CustoOperacionalRequestDTO {
    private Long idShow;
    private String descricao;
    private BigDecimal valor;
}
