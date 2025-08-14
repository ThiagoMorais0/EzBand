package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ParametroOrcamento {
    private String nome;
    private BigDecimal quantidade;
    private BigDecimal valorCustomizado;
}
