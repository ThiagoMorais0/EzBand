package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.*;
import jdk.jfr.Event;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
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

}
