package com.baseapplication.core.service;

import com.baseapplication.core.dto.PeriodoDTO;
import com.baseapplication.core.dto.metricas.*;

import java.util.List;

public interface MetricasService {
    
    // Métricas para Banda
    MetricaQuantidadeDTO obterQuantidadeShowsBanda(Long idBanda, PeriodoDTO periodo);
    
    MetricaValorDTO obterValorTotalFaturadoBanda(Long idBanda, PeriodoDTO periodo);
    
    MetricaValorDTO obterValorMedioFaturadoBanda(Long idBanda, PeriodoDTO periodo);
    
    List<MetricaShowsPorCidadeDTO> obterShowsPorCidadeBanda(Long idBanda, PeriodoDTO periodo);
    
    List<MetricaShowsPorEstadoDTO> obterShowsPorEstadoBanda(Long idBanda, PeriodoDTO periodo);
    
    MetricaCidadeMaisTocadaDTO obterCidadeMaisTocadaBanda(Long idBanda);
    
    MetricaValorDTO obterValorTotalGastoEnsaiosBanda(Long idBanda, PeriodoDTO periodo);
    
    MetricaValorDTO obterValorMedioGastoEnsaiosBanda(Long idBanda, PeriodoDTO periodo);
    
    // Métricas para Usuário
    MetricaValorDTO obterValorTotalFaturadoUsuario(Long idUsuario, PeriodoDTO periodo);
    
    MetricaQuantidadeDTO obterQuantidadeShowsUsuario(Long idUsuario, PeriodoDTO periodo);
    
    MetricaValorDTO obterValorMedioCachesUsuario(Long idUsuario, PeriodoDTO periodo);
    
    MetricaBandaMaisRentavelDTO obterBandaMaisRentavelUsuario(Long idUsuario);
    
    MetricaBandaMaisShowsDTO obterBandaMaisShowsUsuario(Long idUsuario);
}
