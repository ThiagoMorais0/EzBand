package com.baseapplication.core.dto;

import java.util.List;

import com.baseapplication.core.enums.TipoEvento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AtualizacaoRepertorioEventoDTO {
    private Long idEvento;
    private Long idBanda;
    private TipoEvento tipoEvento;
    private List<RepertorioEventoDTO> musicas;
}
