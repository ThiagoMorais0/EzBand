package com.tms.tmsis.foodhub.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FHProdutoDTO {
    private String codigo;
    private String nome;
    private String descricao;
    private Integer tipo;
    private BigDecimal valorCompra;
    private BigDecimal valorVenda;
    private Boolean ativo;
}

