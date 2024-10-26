package com.baseapplication.core.service;

import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Notificacao;

import java.util.Collection;
import java.util.List;

public interface NotificacaoService {
    List<Notificacao> buscarNotificacoesPorUsuario(Long idUsuario);
    void enviarConviteParaEvento(String contato,
                                 TipoContato tipoContato,
                                 Long idEvento,
                                 TipoEvento tipoEvento,
                                 Long idUsuarioRemetente);
}
