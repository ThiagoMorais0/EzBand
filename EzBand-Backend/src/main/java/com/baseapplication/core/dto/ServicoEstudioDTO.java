package com.baseapplication.core.dto;

import com.baseapplication.core.model.ServicoEstudio;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ServicoEstudioDTO {
    private String nome;
    private String descricao;
    private BigDecimal valor;

    public ServicoEstudioDTO(ServicoEstudio entity){
        BeanUtils.copyProperties(entity, this);
    }

    public ServicoEstudio toEntity(){
        ServicoEstudio entity = new ServicoEstudio();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
