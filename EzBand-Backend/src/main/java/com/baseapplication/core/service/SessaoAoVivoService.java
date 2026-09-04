package com.baseapplication.core.service;

import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import com.baseapplication.core.dto.live.ResumoSessaoDTO;
import com.baseapplication.core.enums.TipoEvento;

import java.util.List;

/** Persistência do resumo pós-show. */
public interface SessaoAoVivoService {

    /**
     * Grava o resumo de uma sessão encerrada e devolve o id.
     *
     * <p>Chamado uma única vez por show. Devolve {@code null} quando não houve nada digno de
     * registro (nenhuma faixa aberta) ou quando aquele resumo já foi gravado.
     */
    Long persistir(LiveSessionSnapshot snapshot);

    ResumoSessaoDTO buscarUltimoDoEvento(Long idEvento, TipoEvento tipoEvento);

    List<ResumoSessaoDTO> buscarDoEvento(Long idEvento, TipoEvento tipoEvento);
}
