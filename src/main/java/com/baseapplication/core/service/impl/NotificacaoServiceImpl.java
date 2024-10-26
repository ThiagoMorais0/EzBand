package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.NotificacaoDao;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.enums.TipoNotificacao;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.EventoService;
import com.baseapplication.core.service.NotificacaoService;
import com.baseapplication.core.service.UsuarioService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NotificacaoServiceImpl implements NotificacaoService {

    @Autowired
    private NotificacaoDao notificacaoDao;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private EventoService eventoService;

    @Override
    public List<Notificacao> buscarNotificacoesPorUsuario(Long idUsuario) {
        return null;
    }

    @Override
    public void enviarConviteParaEvento(String contato,
                                        TipoContato tipoContato,
                                        Long idEvento,
                                        TipoEvento tipoEvento,
                                        Long idUsuarioRemetente ) {

        Usuario remetente = usuarioService.buscarPorId(idUsuarioRemetente);
        Usuario destinatario = usuarioService.buscarPorContato(contato, tipoContato);
        Evento evento = eventoService.buscarEvento(idEvento, tipoEvento);
        Notificacao notificacao = criarNotificacaoConviteParaEvento(remetente, destinatario, evento, tipoEvento);
        enviarNotificacao(notificacao);
    }

    public void enviarNotificacao(Notificacao notificacao) {
        notificacaoDao.save(notificacao);
    }

    private Notificacao criarNotificacaoConviteParaEvento(Usuario remetente, Usuario destinatario,
                                                          Evento evento, TipoEvento tipoEvento) {
        Notificacao notificacao;

        switch (tipoEvento) {
            case SHOW -> {
                NotificacaoShow notificacaoShow = new NotificacaoShow();
                notificacaoShow.setShow((Show) evento);
                notificacaoShow.setBanda(evento.getBanda());
                notificacao = notificacaoShow;
            }
            case ENSAIO -> {
                NotificacaoEnsaio notificacaoEnsaio = new NotificacaoEnsaio();
                notificacaoEnsaio.setEnsaio((Ensaio) evento);
                notificacaoEnsaio.setBanda(evento.getBanda());
                notificacao = notificacaoEnsaio;
            }
            default -> throw new ServiceException("Tipo de evento " + tipoEvento.getDescricao() + " inválido.");
        }

        configurarNotificacaoComum(notificacao, remetente, destinatario, evento);
        return notificacao;
    }

    private void configurarNotificacaoComum(Notificacao notificacao, Usuario remetente, Usuario destinatario,
                                            Evento evento) {
        notificacao.setRemetente(remetente);
        notificacao.setDestinatario(destinatario);
        notificacao.setMensagem(criarMensagemConviteEvento(evento, remetente));
        notificacao.setTipoNotificacao(TipoNotificacao.CONVITE_PARA_EVENTO);
        notificacao.setData(LocalDate.now());
    }


    private String criarMensagemConviteEvento(Evento evento, Usuario remetente) {
        return remetente.getNome() + " está te convidando para um " +
                evento.getTipoEvento().getDescricao().toUpperCase() +
                " com a banda " + evento.getBanda().getNome() +
                " na data " + evento.getData();
    }
}
