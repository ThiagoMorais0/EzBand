package com.baseapplication.core.service;

import com.baseapplication.core.controller.NovoEnsaioDTO;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;

import java.time.LocalDate;
import java.util.List;

public interface EventoService {
    Evento buscarPorId(Long idEvento, TipoEvento tipoEvento);
    EventosSeparadosDTO buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario);

    Evento buscarEvento(Long idEvento, TipoEvento tipoEvento);

    void salvar(Evento evento);

    InformacoesEventoDTO buscarPobuscarInformacoesEventorId(Long idEvento, TipoEvento tipoEvento);

    void atualizarInformacoesEvento(InformacoesEventoDTO informacoesEventoDTO);

    MusicoEventoDTO buscarMusicoParaEvento(String contato, TipoContato tipoContato);

    void enviarConviteParaEvento(ConviteEventoDTO conviteEvento);

    List<RepertorioEventoDTO> buscarRepertorioEvento(Long idEvento, TipoEvento tipoEvento);

    void atualizarRepertorioEvento(AtualizacaoRepertorioEventoDTO atualizacaoRepertorio);

    List<DisponibilidadeMusicoParaEventoDTO> buscarMembrosEDisponibilidadeParaShow(Long idBanda, LocalDate data);

    void marcarShow(NovoShowDTO novoShowDTO);

    void marcarEnsaio(NovoEnsaioDTO novoEnsaioDTO);

    Boolean isNotificacaoShowAceitaPorTodosMembros(Long idShow);

    void aceitarNotificacao(Long idNotificacao);

    void recusarNotificacao(Long idNotificacao);
}
