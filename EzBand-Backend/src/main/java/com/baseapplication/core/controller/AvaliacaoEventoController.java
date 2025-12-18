package com.baseapplication.core.controller;

import com.baseapplication.core.dto.AvaliacaoEventoDTO;
import com.baseapplication.core.dto.PendenciaAvaliacaoEventoDTO;
import com.baseapplication.core.dto.ResumoPendenciasAvaliacaoDTO;
import com.baseapplication.core.service.AvaliacaoEventoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacao-evento")
@RequiredArgsConstructor
public class AvaliacaoEventoController {
    
    private final AvaliacaoEventoService avaliacaoEventoService;
    
    /**
     * Busca todas as pendências de avaliação das bandas do usuário logado
     * GET /avaliacao-evento/pendencias
     */
    @GetMapping("/pendencias")
    public ResponseEntity<ResumoPendenciasAvaliacaoDTO> buscarPendenciasDoUsuario() {
        try {
            ResumoPendenciasAvaliacaoDTO resumo = avaliacaoEventoService.buscarPendenciasDoUsuario();
            return ResponseEntity.ok(resumo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca pendências de avaliação de uma banda específica
     * GET /avaliacao-evento/pendencias/banda/{idBanda}
     */
    @GetMapping("/pendencias/banda/{idBanda}")
    public ResponseEntity<List<PendenciaAvaliacaoEventoDTO>> buscarPendenciasPorBanda(
            @PathVariable Long idBanda) {
        try {
            List<PendenciaAvaliacaoEventoDTO> pendencias = 
                avaliacaoEventoService.buscarPendenciasPorBanda(idBanda);
            return ResponseEntity.ok(pendencias);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Conta o número de pendências não avaliadas do usuário
     * GET /avaliacao-evento/pendencias/contador
     */
    @GetMapping("/pendencias/contador")
    public ResponseEntity<Long> contarPendenciasDoUsuario() {
        try {
            Long contador = avaliacaoEventoService.contarPendenciasDoUsuario();
            return ResponseEntity.ok(contador);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Adia uma pendência de avaliação
     * POST /avaliacao-evento/pendencias/{idPendencia}/adiar
     */
    @PostMapping("/pendencias/{idPendencia}/adiar")
    public ResponseEntity<?> adiarPendencia(@PathVariable Long idPendencia) {
        try {
            avaliacaoEventoService.adiarPendencia(idPendencia);
            return ResponseEntity.ok("Pendência adiada com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao adiar pendência");
        }
    }
    
    /**
     * Registra uma avaliação de evento
     * POST /avaliacao-evento/avaliar
     */
    @PostMapping("/avaliar")
    public ResponseEntity<?> avaliarEvento(@Valid @RequestBody AvaliacaoEventoDTO avaliacaoDTO) {
        try {
            AvaliacaoEventoDTO resultado = avaliacaoEventoService.avaliarEvento(avaliacaoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erro ao registrar avaliação");
        }
    }
    
    /**
     * Busca avaliações de um evento específico
     * GET /avaliacao-evento/evento/{idEvento}?tipoEvento=SHOW
     */
    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<?> buscarAvaliacoesDoEvento(
            @PathVariable Long idEvento,
            @RequestParam String tipoEvento) {
        try {
            List<AvaliacaoEventoDTO> avaliacoes = 
                avaliacaoEventoService.buscarAvaliacoesDoEvento(idEvento, tipoEvento);
            return ResponseEntity.ok(avaliacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Busca todas as avaliações de uma banda
     * GET /avaliacao-evento/banda/{idBanda}
     */
    @GetMapping("/banda/{idBanda}")
    public ResponseEntity<?> buscarAvaliacoesDaBanda(@PathVariable Long idBanda) {
        try {
            List<AvaliacaoEventoDTO> avaliacoes = 
                avaliacaoEventoService.buscarAvaliacoesDaBanda(idBanda);
            return ResponseEntity.ok(avaliacoes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
