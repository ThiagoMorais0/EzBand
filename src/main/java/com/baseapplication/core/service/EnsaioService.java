package com.baseapplication.core.service;

import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.superClasses.Evento;

public interface EnsaioService {
    Ensaio buscarPorId(Long idEvento);
}
