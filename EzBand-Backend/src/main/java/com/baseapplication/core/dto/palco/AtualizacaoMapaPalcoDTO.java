package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Salva metadados e o layout inteiro numa requisicao so -- o editor faz
 * autosave com debounce, entao arrastar tres pecas nao vira tres chamadas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtualizacaoMapaPalcoDTO {

    private String nome;
    private String descricao;
    private String observacoes;
    private Double larguraM;
    private Double profundidadeM;
    private List<LayoutPosicaoDTO> posicoes = new ArrayList<>();
    /** Ajuste manual de pecas soltas; posicao nao enviada mantem o automatico. */
    private List<LayoutItemDTO> itens = new ArrayList<>();
    private List<CadastroItemMapaPalcoDTO> itensGerais;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayoutPosicaoDTO {
        private Long id;
        private Double posX;
        private Double posY;
        private Double escala;
        private Double rotacao;
        private Integer ordemCanal;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LayoutItemDTO {
        private Long id;
        private Double posX;
        private Double posY;
        private Double escala;
        private Double rotacao;
        /** Vinculo por proximidade: muda de dono ao ser solto perto de outro. */
        private Long idPosicao;
        private Boolean desvincular;
    }
}
