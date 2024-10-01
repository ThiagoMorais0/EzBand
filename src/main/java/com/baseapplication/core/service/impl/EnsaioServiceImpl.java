package com.baseapplication.core.service.impl;

import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.service.EnsaioService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnsaioServiceImpl implements EnsaioService {
    @Override
    public Ensaio buscarPorId(Long idEvento) {
        return null;
    }

    @Override
    public List<Ensaio> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
        return null;
    }
}
