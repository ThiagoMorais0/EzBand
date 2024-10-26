package com.baseapplication.core.service;

import com.baseapplication.core.dto.AtualizacaoRepertorioEventoDTO;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioDTO;
import com.baseapplication.core.dto.RepertorioEventoDTO;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.dto.BandaDTO;

import java.util.Collection;
import java.util.List;

public interface BandaService {
    List<Banda> buscarBandasPorUsuario(Long idUsuario);

    List<Banda> buscarParticipacoesEspeciais(Long idUsuario);

    BandaDTO getInfo(Long idBanda);

    void cadastrarUsuario(Long idBanda, Long idUsuario, String instrumentos);

    void expulsarUsuario(Long idBanda, Long idUsuario);

    List<InfoPerfilUsuarioDTO> buscarMembros(Long idBanda);

    void atualizarRepertorioEvento(AtualizacaoRepertorioEventoDTO atualizacaoRepertorio);

    List<RepertorioEventoDTO> buscarRepertorioEvento(Long idEvento, TipoEvento tipoEvento);
}
