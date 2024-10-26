package com.baseapplication.core.service;

import com.baseapplication.core.dto.ConviteEventoDTO;
import com.baseapplication.core.dto.EventosSeparadosDTO;
import com.baseapplication.core.dto.MusicoEventoDTO;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;

public interface EventoService {
    Evento buscarPorId(Long idEvento, TipoEvento tipoEvento);
    EventosSeparadosDTO buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario);

    Evento buscarEvento(Long idEvento, TipoEvento tipoEvento);

    void salvar(Evento evento);

    InformacoesEventoDTO buscarPobuscarInformacoesEventorId(Long idEvento, TipoEvento tipoEvento);

    void atualizarInformacoesEvento(InformacoesEventoDTO informacoesEventoDTO);

    MusicoEventoDTO buscarMusicoParaEvento(String contato, TipoContato tipoContato);

    void enviarConviteParaEvento(ConviteEventoDTO conviteEvento);
}
