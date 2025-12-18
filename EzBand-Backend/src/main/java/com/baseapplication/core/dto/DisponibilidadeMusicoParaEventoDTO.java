package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DisponibilidadeMusicoParaEventoDTO {
    private Long id;
    private String nome;
    private String urlFoto;
    private String instrumento;
    private boolean disponivel = true;
    private String mensagem;
    private List<String> eventos;
    private boolean isFantasma = false;

}
