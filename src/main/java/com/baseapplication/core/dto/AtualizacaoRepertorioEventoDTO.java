package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.RepertorioEvento;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AtualizacaoRepertorioEventoDTO {
    private Long idEvento;
    private Long idBanda;
    private TipoEvento tipoEvento;
    private List<RepertorioEventoDTO> musicas;
}
