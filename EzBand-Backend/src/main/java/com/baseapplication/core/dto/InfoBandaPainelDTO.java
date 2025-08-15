package com.baseapplication.core.dto;

import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;

import java.util.List;

public class InfoBandaPainelDTO {
    List<ShowDTO> shows;
    List<EnsaioDTO> ensaios;
    List<InfoPerfilUsuarioDTO> membros;
    List<MusicaDTO> repertorio;
    AnaliseDadosDTO analiseDados;
    List<EventoDTO> historico;
}
