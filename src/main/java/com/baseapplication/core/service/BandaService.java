package com.baseapplication.core.service;

import com.baseapplication.core.model.Banda;

import java.util.Collection;
import java.util.List;

public interface BandaService {
    List<Banda> buscarBandasPorUsuario(Long idUsuario);

    List<Banda> buscarParticipacoesEspeciais(Long idUsuario);
}
