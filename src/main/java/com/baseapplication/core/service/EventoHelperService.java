package com.baseapplication.core.service;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.superClasses.Evento;

import java.util.Collection;
import java.util.List;

public interface EventoHelperService {
    Evento buscarEvento(Long idEvento, TipoEvento tipoEvento);

    EventosSeparadosDTO buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario);


    List<Show> buscarShowsPendentesPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario);

    List<Show> buscarShowsAguardandoPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario);

    List<Ensaio> buscarEnsaiosAguardandoPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario);

    List<Ensaio> buscarEnsaiosPendentesPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario);
}
