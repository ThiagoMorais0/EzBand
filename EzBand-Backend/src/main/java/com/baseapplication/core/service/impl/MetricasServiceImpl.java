package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.EnsaioDao;
import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.dto.PeriodoDTO;
import com.baseapplication.core.dto.metricas.*;
import com.baseapplication.core.enums.TipoPeriodo;
import com.baseapplication.core.service.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricasServiceImpl implements MetricasService {

    private final ShowDao showDao;
    private final EnsaioDao ensaioDao;

    @Override
    public MetricaQuantidadeDTO obterQuantidadeShowsBanda(Long idBanda, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        Long quantidade = showDao.contarShowsRealizadosPorPeriodo(idBanda, datas[0], datas[1]);
        return new MetricaQuantidadeDTO(quantidade);
    }

    @Override
    public MetricaValorDTO obterValorTotalFaturadoBanda(Long idBanda, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        BigDecimal valor = showDao.somarValorFaturadoPorPeriodo(idBanda, datas[0], datas[1]);
        return new MetricaValorDTO(valor);
    }

    @Override
    public MetricaValorDTO obterValorMedioFaturadoBanda(Long idBanda, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        BigDecimal valor = showDao.calcularValorMedioPorPeriodo(idBanda, datas[0], datas[1]);
        return new MetricaValorDTO(valor);
    }

    @Override
    public List<MetricaShowsPorCidadeDTO> obterShowsPorCidadeBanda(Long idBanda, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        return showDao.contarShowsPorCidade(idBanda, datas[0], datas[1]);
    }

    @Override
    public List<MetricaShowsPorEstadoDTO> obterShowsPorEstadoBanda(Long idBanda, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        return showDao.contarShowsPorEstado(idBanda, datas[0], datas[1]);
    }

    @Override
    public MetricaCidadeMaisTocadaDTO obterCidadeMaisTocadaBanda(Long idBanda) {
        List<MetricaShowsPorCidadeDTO> cidades = showDao.buscarCidadeMaisTocada(idBanda)
                .stream()
                .filter(cidade -> cidade.getCidade() != null && !cidade.getCidade().isEmpty())
                .toList();
        if (cidades.isEmpty()) {
            return new MetricaCidadeMaisTocadaDTO(null, null, 0L);
        }
        MetricaShowsPorCidadeDTO primeira = cidades.get(0);
        return new MetricaCidadeMaisTocadaDTO(primeira.getCidade(), primeira.getEstado(), primeira.getQuantidade());
    }

    @Override
    public MetricaValorDTO obterValorTotalGastoEnsaiosBanda(Long idBanda, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        BigDecimal valor = ensaioDao.somarValorGastoPorPeriodo(idBanda, datas[0], datas[1]);
        return new MetricaValorDTO(valor);
    }

    @Override
    public MetricaValorDTO obterValorMedioGastoEnsaiosBanda(Long idBanda, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        BigDecimal valor = ensaioDao.calcularValorMedioPorPeriodo(idBanda, datas[0], datas[1]);
        return new MetricaValorDTO(valor);
    }

    @Override
    public MetricaValorDTO obterValorTotalFaturadoUsuario(Long idUsuario, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        BigDecimal valor = showDao.somarCachesUsuarioPorPeriodo(idUsuario, datas[0], datas[1]);
        return new MetricaValorDTO(valor);
    }

    @Override
    public MetricaQuantidadeDTO obterQuantidadeShowsUsuario(Long idUsuario, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        Long quantidade = showDao.contarShowsUsuarioPorPeriodo(idUsuario, datas[0], datas[1]);
        return new MetricaQuantidadeDTO(quantidade);
    }

    @Override
    public MetricaValorDTO obterValorMedioCachesUsuario(Long idUsuario, PeriodoDTO periodo) {
        LocalDate[] datas = calcularPeriodo(periodo);
        BigDecimal valor = showDao.calcularCacheMedioPorPeriodo(idUsuario, datas[0], datas[1]);
        return new MetricaValorDTO(valor);
    }

    @Override
    public MetricaBandaMaisRentavelDTO obterBandaMaisRentavelUsuario(Long idUsuario) {
        List<Object[]> resultado = showDao.buscarBandaMaisRentavelParaUsuario(idUsuario);
        if (resultado.isEmpty()) {
            return new MetricaBandaMaisRentavelDTO(null, null, BigDecimal.ZERO);
        }
        Object[] primeira = resultado.get(0);
        return new MetricaBandaMaisRentavelDTO(
                (Long) primeira[0],
                (String) primeira[1],
                (BigDecimal) primeira[2]
        );
    }

    @Override
    public MetricaBandaMaisShowsDTO obterBandaMaisShowsUsuario(Long idUsuario) {
        List<Object[]> resultado = showDao.buscarBandaMaisShowsParaUsuario(idUsuario);
        if (resultado.isEmpty()) {
            return new MetricaBandaMaisShowsDTO(null, null, 0L);
        }
        Object[] primeira = resultado.get(0);
        return new MetricaBandaMaisShowsDTO(
                (Long) primeira[0],
                (String) primeira[1],
                (Long) primeira[2]
        );
    }

    /**
     * Calcula o período de datas com base no tipo de período informado
     * @param periodo DTO com informações do período
     * @return Array com [dataInicio, dataFim]
     */
    private LocalDate[] calcularPeriodo(PeriodoDTO periodo) {
        LocalDate dataInicio;
        LocalDate dataFim;

        if (periodo.getTipoPeriodo() == TipoPeriodo.TOTAL) {
            // Para TOTAL, considera desde 1900 até 2100
            dataInicio = LocalDate.of(1900, 1, 1);
            dataFim = LocalDate.of(2100, 12, 31);
        } else if (periodo.getTipoPeriodo() == TipoPeriodo.ANO) {
            // Para ANO, considera o ano inteiro
            dataInicio = LocalDate.of(periodo.getAno(), 1, 1);
            dataFim = LocalDate.of(periodo.getAno(), 12, 31);
        } else if (periodo.getTipoPeriodo() == TipoPeriodo.MES) {
            // Para MÊS, considera o mês inteiro
            dataInicio = LocalDate.of(periodo.getAno(), periodo.getMes(), 1);
            dataFim = dataInicio.with(TemporalAdjusters.lastDayOfMonth());
        } else if (periodo.getTipoPeriodo() == TipoPeriodo.SEMANA) {
            // Para SEMANA, calcula a semana do ano
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            dataInicio = LocalDate.of(periodo.getAno(), 1, 1)
                    .with(weekFields.weekOfYear(), periodo.getSemana())
                    .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            dataFim = dataInicio.plusDays(6);
        } else if (periodo.getTipoPeriodo() == TipoPeriodo.DIA) {
            // Para DIA, considera apenas o dia específico
            dataInicio = LocalDate.of(periodo.getAno(), periodo.getMes(), periodo.getDia());
            dataFim = dataInicio;
        } else {
            // Fallback para TOTAL
            dataInicio = LocalDate.of(1900, 1, 1);
            dataFim = LocalDate.of(2100, 12, 31);
        }

        return new LocalDate[]{dataInicio, dataFim};
    }
}
