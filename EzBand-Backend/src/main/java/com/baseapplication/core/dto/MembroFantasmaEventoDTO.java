package com.baseapplication.core.dto;

import com.baseapplication.core.model.MembroFantasmaEvento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MembroFantasmaEventoDTO {
    private Long idMembroFantasma;
    private String nome;
    private String instrumento;
    private String urlFoto;
    private BigDecimal cache = BigDecimal.ZERO;

    public MembroFantasmaEventoDTO(MembroFantasmaEvento membroFantasmaEvento) {
        this.idMembroFantasma = membroFantasmaEvento.getMembroFantasma().getId();
        this.nome = membroFantasmaEvento.getMembroFantasma().getNome();
        this.instrumento = membroFantasmaEvento.getInstrumentos();
        this.urlFoto = membroFantasmaEvento.getMembroFantasma().getUrlFoto();
        this.cache = membroFantasmaEvento.getCache();
    }
}
