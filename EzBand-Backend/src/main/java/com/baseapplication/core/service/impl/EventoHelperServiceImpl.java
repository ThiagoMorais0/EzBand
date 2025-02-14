package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.*;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoHelperServiceImpl implements EventoHelperService {

    @Autowired
    private ShowService showService;

    @Autowired
    private EnsaioService ensaioService;

    @Override
    public Evento buscarEvento(Long idEvento, TipoEvento tipoEvento) {
        switch (tipoEvento){
            case SHOW -> {return showService.buscarPorId(idEvento);}
            case ENSAIO -> {return ensaioService.buscarPorId(idEvento);}
            default -> {throw new ServiceException("Evento não encontrado");}
        }
    }

    @Override
    public EventosSeparadosDTO buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
        return new EventosSeparadosDTO(
                showService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario)
                        .stream().map(ShowDTO::new).collect(Collectors.toList()),
                ensaioService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario)
                        .stream().map(EnsaioDTO::new).collect(Collectors.toList())
        );
    }

    @Override
    public List<Show> buscarShowsPendentesPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
        return showService.buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, StatusEvento.PENDENTE.toString());
    }

    @Override
    public List<Show> buscarShowsAguardandoPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
        return showService.buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, StatusEvento.AGUARDANDO_APROVACAO.toString());
    }

    @Override
    public List<Ensaio> buscarEnsaiosPendentesPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
        return ensaioService.buscarEnsaiosPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, StatusEvento.PENDENTE.toString());
    }

    @Override
    public List<Ensaio> buscarEnsaiosAguardandoPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
        return ensaioService.buscarEnsaiosPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, StatusEvento.AGUARDANDO_APROVACAO.toString());
    }



}
