package com.baseapplication.core.dto;

import com.baseapplication.core.model.dto.EnsaioDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EnsaiosFuturosDTO {
    List<EnsaioDTO> ensaiosPendentes;
    List<EnsaioDTO> ensaiosAguardando;
}
