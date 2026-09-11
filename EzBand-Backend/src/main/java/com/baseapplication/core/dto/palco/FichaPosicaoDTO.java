package com.baseapplication.core.dto.palco;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Ficha tecnica completa de uma posicao. Salva de uma vez so: a lista enviada
 * substitui a anterior inteira, o que evita sincronizar item a item no front.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FichaPosicaoDTO {

    private String observacao;
    private List<CadastroItemMapaPalcoDTO> itens = new ArrayList<>();
}
