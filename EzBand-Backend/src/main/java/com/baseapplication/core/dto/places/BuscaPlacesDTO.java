package com.baseapplication.core.dto.places;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BuscaPlacesDTO {

    /** Texto digitado pelo usuario. Ex: "Bar Brahma". */
    private String texto;

    /**
     * UUID gerado pelo front e reutilizado em todas as buscas ate a selecao de um local.
     * Agrupa a busca numa unica sessao de cobranca do Google.
     */
    private String sessionToken;

    /** Opcionais: enviesam o resultado para perto da cidade da banda. */
    private Double latitude;
    private Double longitude;
}
