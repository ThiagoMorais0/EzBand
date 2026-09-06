package com.baseapplication.core.dto.places;

import com.baseapplication.core.dto.EnderecoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaceDetalheDTO {

    private String placeId;

    /** Nome do local. Vem da sugestao do autocomplete, nao do Place Details (evita SKU Pro). */
    private String nome;

    /** Endereco ja quebrado nos campos do EzBand. */
    private EnderecoDTO endereco;

    /** Endereco completo em uma linha, para exibicao. */
    private String enderecoFormatado;
}
