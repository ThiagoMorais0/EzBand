package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.NotificacaoDao;
import com.baseapplication.core.dao.RespostaNotificacaoDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.NotificacaoDTO;
import com.baseapplication.core.enums.*;
import com.baseapplication.core.event.events.NotificacaoEvent;
import com.baseapplication.core.event.events.resposta.RespostaSolicitacaoAgendarEnsaioEvent;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.factory.RespostaNotificacaoFactory;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.dto.RespostaNotificacaoDTO;
import com.baseapplication.core.model.notificacao.RespostaNotificacao;
import com.baseapplication.core.model.notificacao.SolicitacaoAgendarEnsaio;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificacaoServiceImpl implements NotificacaoService {

    private final NotificacaoDao notificacaoDao;
    private final RespostaNotificacaoDao respostaNotificacaoDao;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final UsuarioDao usuarioDao;
    private final Map<String, Sinks.Many<NotificacaoDTO>> sinks = new ConcurrentHashMap<>();

    @Override
    public void salvarNotificacao(Notificacao notificacao) {
        notificacaoDao.save(notificacao);
    }

    @Override
    public List<Notificacao> buscarNaoLidas(Long destinatarioId, TipoParticipante destinatarioTipo) {
        List<Notificacao> notificacoesUsuario = notificacaoDao.buscarNaoLidas(destinatarioId, destinatarioTipo);
        if(destinatarioTipo.equals(TipoParticipante.USUARIO)) {
            Usuario usuario = usuarioDao.findById(destinatarioId).get();
            for(MusicoBanda musicoBanda : usuario.getMusicoBandaList()) {
                if(musicoBanda.getPermissoes().contains(PermissaoMusico.ADMINISTRADOR)) {
                    List<Notificacao> notificacoesBanda = buscarNaoLidas(musicoBanda.getBanda().getId(), TipoParticipante.BANDA);
                    notificacoesUsuario.addAll(notificacoesBanda);
                }
            }
        }
        return notificacoesUsuario;
    }

    @Override
    public void deletarTodos() {
        notificacaoDao.deleteAll();
    }

    @Override
    public void enviarNotificacao(NotificacaoEvent notificacaoEvent) {
        applicationEventPublisher.publishEvent(notificacaoEvent);
    }

    @Override
    public void responderNotificacao(Long idNotificacao, RespostaNotificacaoDTO respostaDTO) {
        respostaNotificacaoDao.save(respostaDTO.toEntity(idNotificacao));
        criarEEnviarNotificacaoResposta(idNotificacao, respostaDTO);
    }

    private String key(Long id, TipoParticipante tipo) {
        return id + ":" + tipo.name();
    }

    @Override
    public Flux<NotificacaoDTO> streamNotificacoes(Long destinatarioId, TipoParticipante destinatarioTipo) {
        String chave = key(destinatarioId, destinatarioTipo);

        Sinks.Many<NotificacaoDTO> sink = sinks.computeIfAbsent(
                chave,
                k -> Sinks.many().multicast().onBackpressureBuffer()
        );

        // Busca notificações não lidas
        Flux<NotificacaoDTO> naoLidas = Flux.fromIterable(
                buscarNaoLidas(destinatarioId, destinatarioTipo).stream()
                        .map(i -> new NotificacaoDTO(
                                i.getId(),
                                i.getMensagem(),
                                i.getDestinatarioId(),
                                i.getDestinatarioTipo().name(),
                                i.getRemetenteTipo().name(),
                                i.isLida()))
                        .toList()
        );

        // Se for usuário, cria “bridge” para bandas que ele administra
        if (destinatarioTipo == TipoParticipante.USUARIO) {
            Usuario usuario = usuarioDao.findById(destinatarioId).get();

            for (MusicoBanda musicoBanda : usuario.getMusicoBandaList()) {
                if (musicoBanda.getPermissoes().contains(PermissaoMusico.ADMINISTRADOR)) {
                    Long bandaId = musicoBanda.getBanda().getId();
                    String chaveBanda = key(bandaId, TipoParticipante.BANDA);

                    // Conecta o flux da banda ao sink do usuário
                    Sinks.Many<NotificacaoDTO> sinkBanda = sinks.computeIfAbsent(
                            chaveBanda,
                            k -> Sinks.many().multicast().onBackpressureBuffer()
                    );

                    // Toda notificação que cair no sink da banda é replicada para o sink do usuário
                    sinkBanda.asFlux().subscribe(sink::tryEmitNext);
                }
            }
        }

        // Concatena notificações não lidas primeiro, depois segue em tempo real
        return Flux.concat(
                naoLidas,
                sink.asFlux().doFinally(signal -> sinks.remove(chave))
        );
    }

    private void criarEEnviarNotificacaoResposta(Long idNotificacao, RespostaNotificacaoDTO respostaDTO) {
        Notificacao notificacao = notificacaoDao.findById(idNotificacao)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada"));
        Notificacao notificacaoResposta = RespostaNotificacaoFactory.criarNotificacaoResposta(notificacao, respostaDTO.getAcao() );
        notificacaoDao.save(notificacaoResposta);
        enviarNotificacao(new RespostaSolicitacaoAgendarEnsaioEvent(notificacaoResposta));
    }




//    @Autowired
//    private NotificacaoDao notificacaoDao;
//
//    @Autowired
//    private UsuarioHelperService usuarioService;
//
//    @Autowired
//    private EventoHelperService eventoHelperService;
//
//    @Autowired
//    private EstadoNotificacaoService estadoNotificacaoService;
//
//    @Override
//    public List<Notificacao> buscarNotificacoesPorUsuario(Long idUsuario) {
//        List<Notificacao> notificacoes = notificacaoDao.buscarNotificacoesPorUsuario(idUsuario, StatusNotificacao.NAO_VISUALIZADO);
//        return notificacoes != null ? notificacoes : Collections.emptyList();
//    }
//
//    @Override
//    public void enviarConviteParaEvento(String contato,
//                                        TipoContato tipoContato,
//                                        Long idEvento,
//                                        TipoEvento tipoEvento,
//                                        Long idUsuarioRemetente ) {
//
//        Usuario remetente = usuarioService.buscarPorId(idUsuarioRemetente);
//        Usuario destinatario = usuarioService.buscarPorContato(contato, tipoContato);
//        Evento evento = eventoHelperService.buscarEvento(idEvento, tipoEvento);
//        Notificacao notificacao = criarNotificacaoConviteParaEvento(remetente, destinatario, evento, tipoEvento);
//        enviarNotificacao(notificacao);
//    }
//
//    @Override
//    public void enviarSolicitacaoParaIngressarBanda(Banda banda, Usuario remetente, String instrumento) {
//        Notificacao notificacao = montarNotificacaoSolicitacaoParaIngressarBanda(banda, remetente, instrumento);
//        enviarNotificacaoParaTodosAdministradores(banda, notificacao);
//    }
//
//    @Override
//    public List<Notificacao> buscarNotificacoesEventoMembros(Evento evento) {
//        switch (evento.getTipoEvento()){
//            case SHOW -> { return notificacaoDao.buscarNotificacoesShowMembros(evento.getId()); }
//            case ENSAIO -> { return notificacaoDao.buscarNotificacoesEnsaioMembros(evento.getId()); }
//            default -> { throw new ServiceException("Tipo de evento inválido"); }
//        }
//    }
//
//    @Override
//    public void enviar(Notificacao notificacao) {
//        notificacaoDao.save(notificacao);
//    }
//
//    @Override
//    public Notificacao recusarNotificacao(Long idNotificacao) {
//        Notificacao notificacao = notificacaoDao.findById(idNotificacao).orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada."));
//        notificacao.setStatusNotificacao(StatusNotificacao.RECUSADO);
//        return notificacaoDao.save(notificacao);
//    }
//
//    @Override
//    public Notificacao aceitarNotificacao(Long idNotificacao) {
//        Notificacao notificacao = notificacaoDao.findById(idNotificacao).orElseThrow(() -> new ResourceNotFoundException("Notificação não encontrada."));
//        notificacao.setStatusNotificacao(StatusNotificacao.ACEITO);
//        return notificacaoDao.save(notificacao);
//    }
//
//    private Notificacao montarNotificacaoSolicitacaoParaIngressarBanda(Banda banda, Usuario usuario, String instrumento) {
//        NotificacaoUsuario notificacao = new NotificacaoUsuario();
//        notificacao.setRemetente(usuario);
//        notificacao.setMensagem(usuario.getNome() + " está pedindo para entrar na banda!");
//        notificacao.setBandaDestino(banda);
//        notificacao.setData(LocalDate.now());
//        notificacao.setObservacao(instrumento);
//        notificacao.setTipoNotificacao(TipoNotificacao.SOLICITACAO_PARA_INGRESSAR_BANDA);
//        notificacao = notificacaoDao.save(notificacao);
//        return notificacao;
//    }
//
//    private static void enviarNotificacaoParaTodosAdministradores(Banda banda, Notificacao notificacao) {
//        for(MusicoBanda musicoBanda : banda.getMusicos()){
//            if(musicoBanda.getPermissoes().contains(PermissaoMusico.ADMINISTRADOR) ||
//                    musicoBanda.getPermissoes().contains(PermissaoMusico.FUNDADOR)){
//                montarEstadoNotificacaoNova(notificacao, musicoBanda.getUsuario());
//            }
//        }
//    }
//
//    public void enviarNotificacao(Notificacao notificacao) {
//        notificacao = notificacaoDao.save(notificacao);
//    }
//
//    private static EstadoNotificacao montarEstadoNotificacaoNova(Notificacao notificacao, Usuario destinatario) {
//        EstadoNotificacao estadoNotificacao = new EstadoNotificacao();
//        estadoNotificacao.setNotificacao(notificacao);
//        estadoNotificacao.setStatusNotificacao(StatusNotificacao.NAO_VISUALIZADO);
//        estadoNotificacao.setDestinatario(destinatario);
//        return estadoNotificacao;
//    }
//
//    private Notificacao criarNotificacaoConviteParaEvento(Usuario remetente, Usuario destinatario,
//                                                          Evento evento, TipoEvento tipoEvento) {
//        Notificacao notificacao;
//
//        switch (tipoEvento) {
//            case SHOW -> {
//                NotificacaoShow notificacaoShow = new NotificacaoShow();
//                notificacaoShow.setShow((Show) evento);
//                notificacaoShow.setBanda(evento.getBanda());
//                notificacao = notificacaoShow;
//            }
//            case ENSAIO -> {
//                NotificacaoEnsaio notificacaoEnsaio = new NotificacaoEnsaio();
//                notificacaoEnsaio.setEnsaio((Ensaio) evento);
//                notificacaoEnsaio.setBanda(evento.getBanda());
//                notificacao = notificacaoEnsaio;
//            }
//            default -> throw new ServiceException("Tipo de evento " + tipoEvento.getDescricao() + " inválido.");
//        }
//
//        configurarNotificacaoComum(notificacao, remetente, destinatario, evento);
//        return notificacao;
//    }
//
//    private void configurarNotificacaoComum(Notificacao notificacao, Usuario remetente, Usuario destinatario,
//                                            Evento evento) {
//        notificacao.setRemetente(remetente);
//        notificacao.setDestinatario(destinatario);
//        notificacao.setMensagem(criarMensagemConviteEvento(evento, remetente));
//        notificacao.setTipoNotificacao(TipoNotificacao.CONVITE_PARA_EVENTO);
//        notificacao.setData(LocalDate.now());
//    }
//
//
//    private String criarMensagemConviteEvento(Evento evento, Usuario remetente) {
//        return remetente.getNome() + " está te convidando para um " +
//                evento.getTipoEvento().getDescricao().toUpperCase() +
//                " com a banda " + evento.getBanda().getNome() +
//                " na data " + evento.getData();
//    }
}
