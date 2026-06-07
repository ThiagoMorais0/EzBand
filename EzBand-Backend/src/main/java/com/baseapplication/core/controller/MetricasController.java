package com.baseapplication.core.controller;

import com.baseapplication.core.dto.PeriodoDTO;
import com.baseapplication.core.dto.metricas.*;
import com.baseapplication.core.service.MetricasService;
import com.baseapplication.core.utils.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/metricas")
public class MetricasController {

    @Autowired
    private MetricasService metricasService;

    // ==================== MÉTRICAS PARA BANDA ====================

    @PostMapping("/banda/quantidadeShows")
    public ResponseEntity<?> obterQuantidadeShowsBanda(@RequestParam Long idBanda, @RequestBody PeriodoDTO periodo) {
        try {
            MetricaQuantidadeDTO metrica = metricasService.obterQuantidadeShowsBanda(idBanda, periodo);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/banda/valorTotalFaturado")
    public ResponseEntity<?> obterValorTotalFaturadoBanda(@RequestParam Long idBanda, @RequestBody PeriodoDTO periodo) {
        try {
            MetricaValorDTO metrica = metricasService.obterValorTotalFaturadoBanda(idBanda, periodo);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/banda/valorMedioFaturado")
    public ResponseEntity<?> obterValorMedioFaturadoBanda(@RequestParam Long idBanda, @RequestBody PeriodoDTO periodo) {
        try {
            MetricaValorDTO metrica = metricasService.obterValorMedioFaturadoBanda(idBanda, periodo);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/banda/showsPorCidade")
    public ResponseEntity<?> obterShowsPorCidadeBanda(@RequestParam Long idBanda, @RequestBody PeriodoDTO periodo) {
        try {
            List<MetricaShowsPorCidadeDTO> metricas = metricasService.obterShowsPorCidadeBanda(idBanda, periodo);
            return ResponseEntity.ok(metricas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/banda/showsPorEstado")
    public ResponseEntity<?> obterShowsPorEstadoBanda(@RequestParam Long idBanda, @RequestBody PeriodoDTO periodo) {
        try {
            List<MetricaShowsPorEstadoDTO> metricas = metricasService.obterShowsPorEstadoBanda(idBanda, periodo);
            return ResponseEntity.ok(metricas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/banda/cidadeMaisTocada")
    public ResponseEntity<?> obterCidadeMaisTocadaBanda(@RequestParam Long idBanda) {
        try {
            MetricaCidadeMaisTocadaDTO metrica = metricasService.obterCidadeMaisTocadaBanda(idBanda);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/banda/valorTotalGastoEnsaios")
    public ResponseEntity<?> obterValorTotalGastoEnsaiosBanda(@RequestParam Long idBanda, @RequestBody PeriodoDTO periodo) {
        try {
            MetricaValorDTO metrica = metricasService.obterValorTotalGastoEnsaiosBanda(idBanda, periodo);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/banda/valorMedioGastoEnsaios")
    public ResponseEntity<?> obterValorMedioGastoEnsaiosBanda(@RequestParam Long idBanda, @RequestBody PeriodoDTO periodo) {
        try {
            MetricaValorDTO metrica = metricasService.obterValorMedioGastoEnsaiosBanda(idBanda, periodo);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== MÉTRICAS PARA USUÁRIO ====================

    @PostMapping("/usuario/valorTotalFaturado")
    public ResponseEntity<?> obterValorTotalFaturadoUsuario(@RequestBody PeriodoDTO periodo) {
        try {
            Long idUsuario = Context.getUsuarioLogado().getId();
            MetricaValorDTO metrica = metricasService.obterValorTotalFaturadoUsuario(idUsuario, periodo);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/usuario/quantidadeShows")
    public ResponseEntity<?> obterQuantidadeShowsUsuario(@RequestBody PeriodoDTO periodo) {
        try {
            Long idUsuario = Context.getUsuarioLogado().getId();
            MetricaQuantidadeDTO metrica = metricasService.obterQuantidadeShowsUsuario(idUsuario, periodo);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/usuario/valorMedioCaches")
    public ResponseEntity<?> obterValorMedioCachesUsuario(@RequestBody PeriodoDTO periodo) {
        try {
            Long idUsuario = Context.getUsuarioLogado().getId();
            MetricaValorDTO metrica = metricasService.obterValorMedioCachesUsuario(idUsuario, periodo);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/usuario/bandaMaisRentavel")
    public ResponseEntity<?> obterBandaMaisRentavelUsuario() {
        try {
            Long idUsuario = Context.getUsuarioLogado().getId();
            MetricaBandaMaisRentavelDTO metrica = metricasService.obterBandaMaisRentavelUsuario(idUsuario);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/usuario/bandaMaisShows")
    public ResponseEntity<?> obterBandaMaisShowsUsuario() {
        try {
            Long idUsuario = Context.getUsuarioLogado().getId();
            MetricaBandaMaisShowsDTO metrica = metricasService.obterBandaMaisShowsUsuario(idUsuario);
            return ResponseEntity.ok(metrica);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
