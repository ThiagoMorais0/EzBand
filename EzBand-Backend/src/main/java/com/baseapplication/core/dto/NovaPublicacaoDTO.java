package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoParticipante;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NovaPublicacaoDTO {
    private String texto;
    private TipoParticipante tipoPublicante;
    private Long idPublicante;
}
