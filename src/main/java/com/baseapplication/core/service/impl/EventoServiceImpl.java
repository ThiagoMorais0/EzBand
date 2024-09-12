package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.EnsaioService;
import com.baseapplication.core.service.EventoService;
import com.baseapplication.core.service.ShowService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private ShowService showService;

    @Autowired
    private EnsaioService ensaioService;

    @Override
    public Evento buscarPorId(Long idEvento, TipoEvento tipoEvento) {
        switch (tipoEvento){
            case SHOW -> { return showService.buscarPorId(idEvento); }
            case ENSAIO -> { return ensaioService.buscarPorId(idEvento); }
            default -> { throw new ServiceException("Tipo de evento inválido"); }
        }
    }
}
