package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Salva metadados e o layout inteiro numa requisicao so -- o editor faz
 * autosave com debounce, entao arrastar tres blocos nao vira tres chamadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizacaoMapaPalcoDTO {

    private String nome;
    private String descricao;
    private String observacoes;
    private Integer gradeColunas;
    private Integer gradeLinhas;
    private List<LayoutPosicaoDTO> posicoes = new ArrayList<>();
    private List<CadastroItemMapaPalcoDTO> itensGerais;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayoutPosicaoDTO {
        private Long id;
        private Integer coluna;
        private Integer linha;
        private Integer larguraCel;
        private Integer alturaCel;
        private Integer ordemCanal;
    }
}
