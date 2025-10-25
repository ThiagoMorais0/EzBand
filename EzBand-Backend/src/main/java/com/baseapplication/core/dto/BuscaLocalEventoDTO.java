package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuscaLocalEventoDTO {
    private String nome;
    private String cidade;
    private String rua;
    private String bairro;
    private String UF;
}
