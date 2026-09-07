package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventoConviteDTO {
    private Long id;
    private TipoEvento tipoEvento;
}
