package com.baseapplication.core.model.dto;

import com.baseapplication.core.enums.TipoCalculo;
import com.baseapplication.core.model.ParametroCusto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ParametroCustoDTO {
    private Long id;
    private BandaDTO banda = new BandaDTO();
    private String nome;
    private String unidade;
    private BigDecimal valorUnitario;
    private String tipoCalculo;

    public ParametroCustoDTO(ParametroCusto entity){
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.unidade = entity.getUnidade();
        this.valorUnitario = entity.getValorUnitario();
        this.tipoCalculo = entity.getTipoCalculo().name();
        this.banda = new BandaDTO(entity.getBanda());
    }

    public ParametroCusto toEntity(){
        ParametroCusto entity = new ParametroCusto();
        entity.setId(this.getId());
        entity.getBanda().setId(this.getBanda().getId());
        entity.setNome(this.getNome());
        entity.setUnidade(this.getUnidade());
        entity.setValorUnitario(this.getValorUnitario());
        entity.setTipoCalculo(TipoCalculo.findByNome(this.getTipoCalculo()));
        return entity;
    }
}
