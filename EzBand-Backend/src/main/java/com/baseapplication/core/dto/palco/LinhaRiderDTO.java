package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Linha das listas "a banda leva" / "esperamos do local" / "energia". */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinhaRiderDTO {

    private String categoria;
    private Integer quantidade;
    private String descricao;
    /** Marca/modelo, voltagem, ponto -- o que qualifica a linha. */
    private String detalhe;
    /** De quem e a necessidade. Nulo = item geral do palco. */
    private String posicao;
}
