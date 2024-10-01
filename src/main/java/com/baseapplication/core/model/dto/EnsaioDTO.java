package com.baseapplication.core.model.dto;

import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EnsaioDTO extends EventoDTO {
    private BigDecimal valor;

    public EnsaioDTO(Ensaio ensaio){
        BeanUtils.copyProperties(ensaio, this);
    }
}
