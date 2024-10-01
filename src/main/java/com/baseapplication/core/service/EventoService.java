package com.baseapplication.core.service;

import com.baseapplication.core.dto.EventosSeparadosDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import com.baseapplication.core.model.superClasses.Evento;

import java.util.List;

public interface EventoService {
    Evento buscarPorId(Long idEvento, TipoEvento tipoEvento);
    EventosSeparadosDTO buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario);
}
