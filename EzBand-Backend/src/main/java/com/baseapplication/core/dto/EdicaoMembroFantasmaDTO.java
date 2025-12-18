package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EdicaoMembroFantasmaDTO {
    private Long id;
    private String nome;
    private String instrumento;
    private String urlFoto;
    private String observacoes;
}
