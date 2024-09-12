package com.baseapplication.core.model.dto;

import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EnsaioDTO extends EventoDTO {
    private BigDecimal valor;
}
