package com.baseapplication.core.dto.palco;

import com.baseapplication.core.enums.OrigemItemPalco;
import com.baseapplication.core.enums.TipoItemPalco;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroItemMapaPalcoDTO {

    private TipoItemPalco tipo;
    private String modelo;
    /** Dono da peca. Preenchido pelo vinculo por proximidade ao soltar. */
    private Long idPosicao;
    private OrigemItemPalco origem;
    private Integer quantidade;
    private String rotulo;
    private String marcaModelo;
    private String observacao;
    private Integer ordem;
    private Integer canais;
    private Integer voltagem;
    private Integer vias;
    private Boolean mixIndependente;
    private String ponto;
    private Double posX;
    private Double posY;
    private Double escala;
    private Double rotacao;
}
