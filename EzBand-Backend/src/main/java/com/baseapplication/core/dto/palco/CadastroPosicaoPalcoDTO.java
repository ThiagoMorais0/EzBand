package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroPosicaoPalcoDTO {

    private String rotulo;
    private String instrumento;
    private String modelo;
    private Double posX;
    private Double posY;
    private Double escala;
    private Double rotacao;
    private Long idUsuario;
    private Long idMembroFantasma;
    private Boolean backingVocal;
    private Integer ordemCanal;
    private String observacao;
}
