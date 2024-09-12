package com.baseapplication.core.service;

import com.baseapplication.core.model.superClasses.Notificacao;

import java.util.Collection;
import java.util.List;

public interface NotificacaoService {
    List<Notificacao> buscarNotificacoesPorUsuario(Long idUsuario);
}
