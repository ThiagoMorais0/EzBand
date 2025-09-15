package com.baseapplication.core.dto;

import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipacoesEspeciaisDTO {
    List<ShowDTO> shows;
    List<EnsaioDTO> ensaios;
}
