package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CadastroItemChecklistBandaDTO {
    
    private Long idBanda;
    private String nome;
    private String descricao;
}
