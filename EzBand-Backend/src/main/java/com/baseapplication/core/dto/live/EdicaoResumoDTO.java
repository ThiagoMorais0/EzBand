package com.baseapplication.core.dto.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Resumo do show corrigido pela banda.
 *
 * <p>Declarativo de propósito: o cliente manda a lista final de faixas, na ordem final, e o
 * servidor faz o diff. Remover uma repetição, acertar uma duração, acrescentar a música que
 * ninguém abriu no app e reordenar tudo é um único salvamento — uma transação, uma checagem
 * de permissão, um rastro de edição. Quatro endpoints granulares dariam quatro estados
 * intermediários que ninguém pediu.
 *
 * <p>Linha com {@code id} é uma faixa existente (atualizada); sem {@code id}, é uma faixa que
 * a banda tocou mas o app não registrou. Faixa que existia e não veio na lista foi removida.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdicaoResumoDTO {

    private List<FaixaEditadaDTO> faixas = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FaixaEditadaDTO {

        /** Null para faixa nova. */
        private Long id;

        private String titulo;
        private String artista;

        /** Editável: a faixa esquecida aberta na tela infla o tempo real dela. */
        private Integer duracaoRealSegundos;

        public boolean temTitulo() {
            return titulo != null && !titulo.isBlank();
        }
    }
}
