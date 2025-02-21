package com.baseapplication.core.service;

import java.util.List;

import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;

public interface NotificacaoService {
    List<Notificacao> buscarNotificacoesPorUsuario(Long idUsuario);
    void enviarConviteParaEvento(String contato,
                                 TipoContato tipoContato,
                                 Long idEvento,
                                 TipoEvento tipoEvento,
                                 Long idUsuarioRemetente);

    void enviarSolicitacaoParaIngressarBanda(Banda banda, Usuario usuario, String instrumento);

    List<Notificacao> buscarNotificacoesEventoMembros(Evento evento);

    void enviar(Notificacao notificacao);

    Notificacao aceitarNotificacao(Long idNotificacao);

    Notificacao recusarNotificacao(Long idNotificacao);
}
