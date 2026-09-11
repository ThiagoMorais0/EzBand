package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Semente de mapa. Carregado dos JSONs em resources/templates-palco e usado
 * tanto para criar o mapa quanto para desenhar a previa no card de escolha --
 * um so lugar define como cada formacao comeca.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateMapaPalcoDTO {

    private String chave;
    private String nome;
    private String descricao;
    private Double larguraM;
    private Double profundidadeM;
    private Integer gradeLinhas;
    private List<PosicaoTemplateDTO> posicoes = new ArrayList<>();
    private List<CadastroItemMapaPalcoDTO> itensGerais = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PosicaoTemplateDTO {
        private String rotulo;
        private String instrumento;
        private Double posX;
        private Integer linha;
        private Double escala;
        private Boolean backingVocal;
        private Integer ordemCanal;
        private List<CadastroItemMapaPalcoDTO> itens = new ArrayList<>();
    }
}
