package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmacaoTokenDTO {
    private String celular;
    private String token;
    private Long idMembroFantasma;
    private Long idUsuario;
}
