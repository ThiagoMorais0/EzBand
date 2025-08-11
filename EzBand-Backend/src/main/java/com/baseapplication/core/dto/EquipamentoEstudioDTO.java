package com.baseapplication.core.dto;

import com.baseapplication.core.model.EquipamentoEstudio;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
public class EquipamentoEstudioDTO {
    private String marca;
    private String modelo;
    private String observacao;
    private Boolean ativo;
    private Integer quantidade;

    public EquipamentoEstudioDTO(EquipamentoEstudio entity){
        BeanUtils.copyProperties(entity, this);
    }

    public EquipamentoEstudio toEntity(){
        EquipamentoEstudio entity = new EquipamentoEstudio();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
