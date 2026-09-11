package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Numeros derivados do mapa. Calculado so aqui no backend e devolvido dentro do
 * agregado: assim os badges do editor e o PDF leem sempre o mesmo numero, sem
 * duas implementacoes do mesmo calculo divergindo com o tempo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoTecnicoDTO {

    private Integer totalPosicoes;
    private Integer posicoesPendentes;
    private Integer totalCanais;
    private Integer viasRetorno;
    private Integer mixesIndependentes;
    private Integer tomadas110;
    private Integer tomadas220;
    private Integer itensDoLocal;
    private Integer itensProprios;
}
