package com.baseapplication.core.dto;

import com.baseapplication.core.model.EquipamentoLocalEvento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
public class EquipamentoLocalEventoDTO {
    private String marca;
    private String modelo;
    private String observacao;
    private Boolean ativo;
    private Integer quantidade;

    public EquipamentoLocalEventoDTO(EquipamentoLocalEvento entity){
        BeanUtils.copyProperties(entity, this);
    }

    public EquipamentoLocalEvento toEntity(){
        EquipamentoLocalEvento entity = new EquipamentoLocalEvento();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
