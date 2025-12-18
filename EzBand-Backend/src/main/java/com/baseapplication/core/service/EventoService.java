package com.baseapplication.core.service;

import com.baseapplication.core.dto.NovoEnsaioDTO;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.superClasses.Evento;
import org.springframework.http.ResponseEntity;

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

    ResponseEntity<?> buscarMembrosEDisponibilidadeParaShow(Long idBanda, LocalDate data);

    void marcarShow(NovoShowDTO novoShowDTO);

    void marcarEnsaio(NovoEnsaioDTO novoEnsaioDTO);

    Boolean isNotificacaoShowAceitaPorTodosMembros(Long idShow);

    void aceitarNotificacao(Long idNotificacao);

    void recusarNotificacao(Long idNotificacao);

    List<Evento> buscarComDataAnteriorAHoje();

    void cancelarEvento(Long idEvento, TipoEvento tipoEvento);

    void atualizarMusicaRepertorio(AtualizacaoMusicaRepertorioDTO atualizacaoMusicaRepertorio);

    void incluirUsuarioNoEvento(Usuario usuario, Evento evento);

    void incluirUsuarioNoEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuarioConvidado);

    void removerMusicaDoRepertorio(Long idEvento, TipoEvento tipoEvento, Integer indice);

    void editarShow(NovoShowDTO novoShowDTO, Long idShow);

    void editarEnsaio(NovoEnsaioDTO novoEnsaioDTO, Long idEnsaio);
}
