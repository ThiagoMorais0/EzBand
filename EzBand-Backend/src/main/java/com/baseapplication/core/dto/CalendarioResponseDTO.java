package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CalendarioResponseDTO {
    private List<CalendarioEventoDTO> eventos;
    private List<CompromissoPessoalDTO> compromissos;
}
