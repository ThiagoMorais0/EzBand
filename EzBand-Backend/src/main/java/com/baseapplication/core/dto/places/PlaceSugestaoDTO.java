package com.baseapplication.core.dto.places;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceSugestaoDTO {

    private String placeId;

    /** Nome do estabelecimento, vindo de structuredFormat.mainText. */
    private String nome;

    /** Resumo do endereco, vindo de structuredFormat.secondaryText. */
    private String endereco;
}
