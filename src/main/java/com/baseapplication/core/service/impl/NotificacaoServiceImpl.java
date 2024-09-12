package com.baseapplication.core.service.impl;

import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.NotificacaoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {
    @Override
    public List<Notificacao> buscarNotificacoesPorUsuario(Long idUsuario) {
        return null;
    }
}
