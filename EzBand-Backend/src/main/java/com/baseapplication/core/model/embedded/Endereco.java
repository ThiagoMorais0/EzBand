package com.baseapplication.core.model.embedded;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Endereco {
    private String pais = "";
    private String estado = "";
    private String cidade = "";
    private String bairro = "";
    private String rua = "";
    private String numero = "";
    private String cep = "";
    private String complemento = "";

    // Preenchidos quando o endereco vem da busca do Google Places. Sempre opcionais:
    // local que nao existe no Google e preenchimento manual deixam os tres nulos.
    private String googlePlaceId;
    private Double latitude;
    private Double longitude;
}
