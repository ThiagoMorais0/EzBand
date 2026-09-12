package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Uma linha do mapa de canais que o tecnico de som recebe. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinhaInputListDTO {

    private Integer canal;
    private String fonte;
    private String posicao;
    /** Como e captado: microfone, DI, linha. */
    private String captacao;
    private String origem;
}
