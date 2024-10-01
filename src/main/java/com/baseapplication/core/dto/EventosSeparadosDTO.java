package com.baseapplication.core.dto;

import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EventosSeparadosDTO {
    List<ShowDTO> shows = new ArrayList<>();
    List<EnsaioDTO> ensaios = new ArrayList<>();
}
