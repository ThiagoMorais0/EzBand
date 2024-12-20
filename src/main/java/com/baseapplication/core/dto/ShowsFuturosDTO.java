package com.baseapplication.core.dto;

import com.baseapplication.core.model.dto.ShowDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ShowsFuturosDTO {
    List<ShowDTO> showsPendentes;
    List<ShowDTO> showsAguardando;
}
