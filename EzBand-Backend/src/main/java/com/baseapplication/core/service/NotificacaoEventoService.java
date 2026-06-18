package com.baseapplication.core.service;

import com.baseapplication.core.enums.TipoEvento;

public interface NotificacaoEventoService {
    
    void notificarNovoEvento(Long idEvento, TipoEvento tipoEvento);

    void notificarNovoEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuarioCriador);
    
    void verificarENotificarEventosProximos();
}
