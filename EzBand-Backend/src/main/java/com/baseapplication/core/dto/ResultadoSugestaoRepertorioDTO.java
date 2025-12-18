package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoSugestaoRepertorioDTO {
    private List<MusicaSugeridaDTO> musicasSugeridas;
    private Integer duracaoTotalMinutos;
    private Integer duracaoSolicitadaMinutos;
    private Integer quantidadeMusicas;
    private String mensagem;
}
