package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.MusicoEventoDao;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.MusicoEventoId;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.MusicoEventoService;
import com.google.protobuf.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MusicoEventoServiceImpl implements MusicoEventoService {

    @Autowired
    private MusicoEventoDao musicoEventoDao;

    @Override
    public void removerTodosParticipantes(Evento evento) {
        musicoEventoDao.removerTodosParticipantes(evento.getId(), evento.getTipoEvento());
    }

    @Override
    public void salvar(MusicoEvento musicoEvento) {
        musicoEventoDao.save(musicoEvento);
    }

    @Override
    public MusicoEvento buscar(Long idEvento, TipoEvento tipoEvento, Long idUsuario) {
        return musicoEventoDao.findById(new MusicoEventoId(idEvento, idUsuario, tipoEvento)).orElseThrow();
    }
}
