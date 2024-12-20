package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.EstadoNotificacaoDao;
import com.baseapplication.core.model.EstadoNotificacao;
import com.baseapplication.core.service.EstadoNotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EstadoNotificacaoServiceImpl implements EstadoNotificacaoService {
    @Autowired
    EstadoNotificacaoDao estadoNotificacaoDao;
    @Override
    public void salvar(EstadoNotificacao estadoNotificacao) {
        estadoNotificacaoDao.save(estadoNotificacao);
    }
}
