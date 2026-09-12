package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroMapaPalcoDTO {

    private String nome;
    private String descricao;
    /** Chave do template semente (ex: "power-trio"). Nulo cria mapa vazio. */
    private String template;
}
