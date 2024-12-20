package com.baseapplication.core.service.impl;

import com.baseapplication.core.controller.NovoEnsaioDTO;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.*;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.*;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.DateUtils;
import com.fasterxml.jackson.databind.util.BeanUtil;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoServiceImpl implements EventoService {

    @Autowired
    private ShowService showService;

    @Autowired
    private EnsaioService ensaioService;

    @Autowired
    private MusicoEventoService musicoEventoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private BandaService bandaService;

    @Override
    public Evento buscarPorId(Long idEvento, TipoEvento tipoEvento) {
        switch (tipoEvento){
            case SHOW -> { return showService.buscarPorId(idEvento); }
            case ENSAIO -> { return ensaioService.buscarPorId(idEvento); }
            default -> { throw new ServiceException("Tipo de evento inválido"); }
        }
    }

    @Override
    public EventosSeparadosDTO buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
        return new EventosSeparadosDTO(
                showService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario)
                        .stream().map(ShowDTO::new).collect(Collectors.toList()),
                ensaioService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario)
                        .stream().map(EnsaioDTO::new).collect(Collectors.toList())
        );
    }

    @Override
    public Evento buscarEvento(Long idEvento, TipoEvento tipoEvento) {
        switch (tipoEvento){
            case SHOW -> {return showService.buscarPorId(idEvento);}
            case ENSAIO -> {return ensaioService.buscarPorId(idEvento);}
            default -> {throw new ServiceException("Evento não encontrado");}
        }
    }

    @Override
    public void salvar(Evento evento) {
        switch (evento.getTipoEvento()){
            case SHOW -> {showService.salvar((Show) evento);}
            case ENSAIO -> {ensaioService.salvar((Ensaio) evento);}
            default -> {throw new ServiceException("Evento não encontrado");}
        }
    }

    @Override
    public InformacoesEventoDTO buscarPobuscarInformacoesEventorId(Long idEvento, TipoEvento tipoEvento) {
        Evento evento = buscarEvento(idEvento, tipoEvento);

        if(evento instanceof Show)
            return new InformacoesShowDTO((Show) evento);
        if(evento instanceof Ensaio)
            return new InformacoesEnsaioDTO((Ensaio) evento);

        return null;
    }

    @Override
    public void atualizarInformacoesEvento(InformacoesEventoDTO informacoesEventoDTO) {
        Evento evento = buscarEvento(informacoesEventoDTO.getId(), informacoesEventoDTO.getTipoEvento());
        atualizarParticipantesEvento(evento, informacoesEventoDTO.getIdUsuariosParticipantes());
        BeanUtils.copyProperties(informacoesEventoDTO, evento);
        salvar(evento);
    }

    @Override
    public MusicoEventoDTO buscarMusicoParaEvento(String contato, TipoContato tipoContato) {
        return new MusicoEventoDTO(usuarioService.buscarPorContato(contato, tipoContato));
    }

    private void atualizarParticipantesEvento(Evento evento, List<Long> idUsuariosParticipantes) {
        removerTodosParticipantesDoEvento(evento);
        idUsuariosParticipantes.forEach(id -> {
            adicionarParticipanteEmEvento(usuarioService.buscarPorId(id), evento);
        });
    }

    private void adicionarParticipanteEmEvento(Usuario usuario, Evento evento) {
        MusicoEvento musicoEvento = new MusicoEvento();
        musicoEvento.setId(new MusicoEventoId(evento.getId(), usuario.getId(), evento.getTipoEvento()));
        //TODO: Verificar como irá receber
        musicoEvento.setInstrumentos("");
        musicoEventoService.salvar(musicoEvento);
    }

    private void removerTodosParticipantesDoEvento(Evento evento) {
        musicoEventoService.removerTodosParticipantes(evento);
    }

    @Override
    public void enviarConviteParaEvento(ConviteEventoDTO conviteEvento) {
        notificacaoService.enviarConviteParaEvento(
                conviteEvento.getContato(),
                conviteEvento.getTipoContato(),
                conviteEvento.getIdEvento(),
                conviteEvento.getTipoEvento(),
                conviteEvento.getIdUsuarioRemetente()
        );
    }

    @Override
    public List<RepertorioEventoDTO> buscarRepertorioEvento(Long idEvento, TipoEvento tipoEvento) {
        return buscarEvento(idEvento, tipoEvento).getRepertorio()
                .stream().map(RepertorioEventoDTO::new).collect(Collectors.toList());
    }

    @Override
    public void atualizarRepertorioEvento(AtualizacaoRepertorioEventoDTO atualizacaoRepertorio) {
        Evento evento = buscarEvento(atualizacaoRepertorio.getIdEvento(),
                atualizacaoRepertorio.getTipoEvento());

        evento.setRepertorio(atualizacaoRepertorio.getMusicas().stream()
                .map(musica -> RepertorioEventoDTO.toEntity(
                        musica,
                        atualizacaoRepertorio.getIdEvento(),
                        atualizacaoRepertorio.getIdBanda(),
                        atualizacaoRepertorio.getTipoEvento()))
                .collect(Collectors.toList()));

        salvar(evento);
    }

    @Override
    public List<DisponibilidadeMusicoParaEventoDTO> buscarMembrosEDisponibilidadeParaShow(Long idBanda, LocalDate data) {
        Banda banda = bandaService.buscarPorId(idBanda);
        List<MusicoBanda> musicos = banda.getMusicos();
        List<DisponibilidadeMusicoParaEventoDTO> musicosParaEvento = new ArrayList<>();
        for(MusicoBanda musico : musicos){
//            if(isMusicoDiferenteDoUsuarioLogado(musico)){
                Evento evento = verificarDisponibilidadeEObterPossivelEvento(musico.getId().getIdUsuario(), data);
                musicosParaEvento.add(montarDisponibilidadeMusicoDTO(musico, evento));
//            }
        }
        return musicosParaEvento;
    }

    @Transactional
    @Override
    public void marcarShow(NovoShowDTO novoShowDTO) {
        Show show = novoShowDTO.toEntity();
        Banda banda = bandaService.buscarPorId(novoShowDTO.getIdBanda());
        show.setBanda(banda);
        if(banda.getParametros().getExigirAprovacaoCompromissos()){
            show.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
        }else{
            show.setStatus(StatusEvento.PENDENTE);
        }

        showService.salvar(show);

        incluirMusicosNoEvento(novoShowDTO.getMusicos(), banda, show,TipoEvento.SHOW);
    }


    @Override
    public void marcarEnsaio(NovoEnsaioDTO novoEnsaioDTO) {
        Ensaio ensaio = novoEnsaioDTO.toEntity();
        Banda banda = bandaService.buscarPorId(novoEnsaioDTO.getIdBanda());
        ensaio.setBanda(banda);
        if(banda.getParametros().getExigirAprovacaoCompromissos()){
            ensaio.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
        }else{
            ensaio.setStatus(StatusEvento.PENDENTE);
        }

        ensaioService.salvar(ensaio);

        incluirMusicosNoEvento(novoEnsaioDTO.getMusicos(), banda, ensaio,TipoEvento.ENSAIO);
    }

    @Override
    public Boolean isNotificacaoShowAceitaPorTodosMembros(Long idShow) {
        List<Notificacao> notificacoes = notificacaoService.buscarNotificacoesEventoMembros(buscarPorId(idShow, TipoEvento.SHOW));

        if(notificacoes.isEmpty())
            throw new ServiceException("Não existem notificações para este evento");

        for(Notificacao notificacao : notificacoes){
            if(!notificacao.getStatusNotificacao().equals(StatusNotificacao.ACEITO))
                return false;
        }
        return true;
    }

    @Override
    public void recusarNotificacao(Long idNotificacao) {
        notificacaoService.recusarNotificacao(idNotificacao);
    }

    @Override
    public void aceitarNotificacao(Long idNotificacao) {
        Notificacao notificacao = notificacaoService.aceitarNotificacao(idNotificacao);
        Evento evento = getEventoFromNotificacao(notificacao);
        verificarSeEventoFoiAprovadoPorTodosMembros(evento);
    }

    private void verificarSeEventoFoiAprovadoPorTodosMembros(Evento evento) {
        if(isNotificacaoEventoAceitaPorTodosMembros(evento)){
            evento.setStatus(StatusEvento.PENDENTE);
            salvar(evento);
        }
    }

    private boolean isNotificacaoEventoAceitaPorTodosMembros(Evento evento) {
        List<Notificacao> notificacoes = notificacaoService.buscarNotificacoesEventoMembros(evento);

        if(notificacoes.isEmpty())
            throw new ServiceException("Não existem notificações para este evento");

        for(Notificacao notificacao : notificacoes){
            if(!notificacao.getStatusNotificacao().equals(StatusNotificacao.ACEITO))
                return false;
        }
        return true;
    }

    private static Evento getEventoFromNotificacao(Notificacao notificacao) {
        Evento evento;
        if(notificacao instanceof NotificacaoShow)
            evento = ((NotificacaoShow) notificacao).getShow();
        else
            evento = ((NotificacaoEnsaio) notificacao).getEnsaio();
        return evento;
    }

    private void incluirMusicosNoEvento(List<MusicoEventoDTO> musicos, Banda banda, Evento evento, TipoEvento tipoEvento) {

        for(MusicoEventoDTO musico : musicos){
            Usuario usuario = usuarioService.buscarPorId(musico.getUsuario().getId());
            MusicoEvento musicoEvento = new MusicoEvento();
            BeanUtils.copyProperties(musico, musicoEvento);

            MusicoEventoId musicoEventoId = new MusicoEventoId();
            musicoEventoId.setIdEvento(evento.getId());
            musicoEventoId.setTipoEvento(tipoEvento);
            musicoEventoId.setIdUsuario(usuario.getId());
            musicoEvento.setId(musicoEventoId);
            musicoEvento.setUsuario(usuario);
            musicoEvento.setInstrumentos(musico.getInstrumento());

            if(evento.getStatus().equals(StatusEvento.AGUARDANDO_APROVACAO)){
                musicoEvento.setSituacao(SituacaoMusicoEvento.CONVITE_PENDENTE);
                if(isMusicoDiferenteDoUsuarioLogado(musicoEvento))
                    enviarNotificacaoAprovacaoEvento(usuario, evento, tipoEvento);
            }else{
                musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
            }

            if(isUsuarioMembroDaBanda(usuario, banda)){
                musicoEventoService.salvar(musicoEvento);
            }else{
                enviarConviteParaEvento(montarConviteEvento(usuario, evento, tipoEvento));
            }
        }

    }

    private void enviarNotificacaoAprovacaoEvento(Usuario usuario, Evento evento, TipoEvento tipoEvento) {
        notificacaoService.enviar(criarNotificacaoAprovacaoEvento(usuario, evento, tipoEvento));
    }

    private Notificacao criarNotificacaoAprovacaoEvento(Usuario usuario, Evento evento, TipoEvento tipoEvento) {
        switch (tipoEvento){
            case SHOW -> { return criarNotificacaoAprovacaoShow(usuario, evento); }
            case ENSAIO -> { return criarNotificacaoAprovacaoEnsaio(usuario, evento); }
            default -> { throw new ServiceException("Tipo inválido"); }
        }
    }

    private Notificacao criarNotificacaoAprovacaoEnsaio(Usuario usuario, Evento evento) {
        NotificacaoEnsaio notificacaoEnsaio = new NotificacaoEnsaio();
        notificacaoEnsaio.setEnsaio((Ensaio) evento);
        notificacaoEnsaio.setDataEnsaio(evento.getData());
        notificacaoEnsaio.setData(LocalDate.now());
        notificacaoEnsaio.setBanda(evento.getBanda());
        notificacaoEnsaio.setMensagem(Context.getUsuarioLogado().getNome() + " está marcando um ensaio em " +
                evento.getLocal() + " - " + evento.getCidade() + " na data " +
                DateUtils.localDateToString(evento.getData()) + ".");
        notificacaoEnsaio.setTipoNotificacao(TipoNotificacao.APROVACAO_EVENTO);
        notificacaoEnsaio.setDestinatario(usuario);
        notificacaoEnsaio.setStatusNotificacao(StatusNotificacao.NAO_VISUALIZADO);
        notificacaoEnsaio.setRemetente(Context.getUsuarioLogado());
        return notificacaoEnsaio;
    }

    private Notificacao criarNotificacaoAprovacaoShow(Usuario usuario, Evento evento) {
        NotificacaoShow notificacaoShow = new NotificacaoShow();
        notificacaoShow.setShow((Show) evento);
        notificacaoShow.setBanda(evento.getBanda());
        notificacaoShow.setData(LocalDate.now());
        notificacaoShow.setDataEvento(evento.getData());
        notificacaoShow.setMensagem(Context.getUsuarioLogado().getNome() + " está marcando com " +
                evento.getBanda().getNome() + " um show em " +
                evento.getLocal() + " - " + evento.getCidade() + " na data " +
                DateUtils.localDateToString(evento.getData()) + ".");
        notificacaoShow.setTipoNotificacao(TipoNotificacao.APROVACAO_EVENTO);
        notificacaoShow.setDestinatario(usuario);
        notificacaoShow.setStatusNotificacao(StatusNotificacao.NAO_VISUALIZADO);
        notificacaoShow.setRemetente(Context.getUsuarioLogado());
        return notificacaoShow;
    }

    private boolean isUsuarioMembroDaBanda(Usuario usuario, Banda banda) {
        for(MusicoBanda musicoBanda : banda.getMusicos()){
            if(musicoBanda.getUsuario().equals(usuario))
                return true;
        }
        return false;
    }

    private ConviteEventoDTO montarConviteEvento(Usuario usuario, Evento evento,TipoEvento tipoEvento) {
        ConviteEventoDTO convite = new ConviteEventoDTO();
        convite.setContato(usuario.getEmail());
        convite.setTipoContato(TipoContato.EMAIL);
        convite.setIdUsuarioRemetente(Context.getUsuarioLogado().getId());
        convite.setTipoEvento(tipoEvento);
        convite.setIdEvento(evento.getId());
        return convite;
    }

    private static DisponibilidadeMusicoParaEventoDTO montarDisponibilidadeMusicoDTO(MusicoBanda musico, Evento evento) {
        DisponibilidadeMusicoParaEventoDTO dto = new DisponibilidadeMusicoParaEventoDTO();
        dto.setId(musico.getUsuario().getId());
        dto.setNome(musico.getUsuario().getNome());
        dto.setUrlFoto(musico.getUsuario().getUrlFotoPerfil());
        dto.setInstrumento(musico.getInstrumentos());
        if(evento != null){
            dto.setDisponivel(false);
            dto.setMensagem("O músico " + musico.getUsuario().getNome() + " já tem show nesta data, às " + evento.getHorarioInicio() + ".");
        }
        return dto;
    }

    private Evento verificarDisponibilidadeEObterPossivelEvento(Long idUsuario, LocalDate data) {
        return showService.buscarPrimeiroPorUsuarioEData(idUsuario, data);
    }

    private boolean isMusicoDiferenteDoUsuarioLogado(MusicoBanda musico) {
        return !musico.getId().getIdUsuario().equals(Context.getUsuarioLogado().getId());
    }

    private boolean isMusicoDiferenteDoUsuarioLogado(MusicoEvento musico) {
        return !musico.getId().getIdUsuario().equals(Context.getUsuarioLogado().getId());
    }

}
