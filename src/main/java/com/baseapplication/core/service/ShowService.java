package com.baseapplication.core.service;

import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.superClasses.Evento;

public interface ShowService {
    Show buscarPorId(Long idEvento);
}
