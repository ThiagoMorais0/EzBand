package com.baseapplication.core.dto;

import com.baseapplication.core.model.MembroFantasma;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MembroFantasmaDTO {
    private Long id;
    private Long idBanda;
    private String nome;
    private String instrumento;
    private String urlFoto;
    private String observacoes;
    private String celular;
    private Boolean celularValidado;

    public MembroFantasmaDTO(MembroFantasma entity) {
        BeanUtils.copyProperties(entity, this);
    }

    public MembroFantasma toEntity() {
        MembroFantasma entity = new MembroFantasma();
        BeanUtils.copyProperties(this, entity);
        return entity;
    }
}
