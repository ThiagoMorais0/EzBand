package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DisponibilidadeMusicoParaEventoDTO {
    private Long id;
    private String nome;
    private String urlFoto;
    private String instrumento;
    private boolean disponivel = true;
    private String mensagem;

}
