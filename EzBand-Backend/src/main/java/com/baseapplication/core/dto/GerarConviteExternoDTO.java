package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GerarConviteExternoDTO {
    private Long idBanda;
    private String instrumento;
    private List<EventoConviteDTO> eventos;
}
