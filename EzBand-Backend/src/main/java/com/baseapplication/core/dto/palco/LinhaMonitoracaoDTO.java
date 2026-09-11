package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinhaMonitoracaoDTO {

    private Integer mix;
    private String posicao;
    private String tipo;
    private Integer vias;
    private String origem;
    private Boolean independente;
}
