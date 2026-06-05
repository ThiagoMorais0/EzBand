package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ValidacaoCelularRequestDTO {
    private String celular;
    private Long idMembroFantasma;
    private Long idUsuario;
}
