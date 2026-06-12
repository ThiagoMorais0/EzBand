package com.baseapplication.core.dto;

import com.baseapplication.core.model.CustoOperacional;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class CustoOperacionalDTO {
    private Long id;
    private String descricao;
    private BigDecimal valor;

    public CustoOperacionalDTO(CustoOperacional custo) {
        this.id = custo.getId();
        this.descricao = custo.getDescricao();
        this.valor = custo.getValor();
    }
}
