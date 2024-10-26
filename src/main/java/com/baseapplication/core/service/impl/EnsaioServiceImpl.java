package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.EnsaioDao;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.service.EnsaioService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnsaioServiceImpl implements EnsaioService {

    @Autowired
    private EnsaioDao ensaioDao;

    @Override
    public Ensaio buscarPorId(Long idEvento) {
        return null;
    }

    @Override
    public List<Ensaio> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
        return null;
    }

    @Override
    public void salvar(Ensaio ensaio) {
        ensaioDao.save(ensaio);
    }
}
