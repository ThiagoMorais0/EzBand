package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.MusicoEventoDao;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.MusicoEventoService;
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
}
