package com.baseapplication.core.dto;

import com.baseapplication.core.model.InstrumentoUsuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentoDTO {
    private Long id;
    private String nome;
    private Boolean favorito;

    public InstrumentoDTO(InstrumentoUsuario instrumento) {
        this.id = instrumento.getId();
        this.nome = instrumento.getNome();
        this.favorito = instrumento.getFavorito();
    }
}
