package com.baseapplication.core.controller;

import com.baseapplication.core.dto.PreferenciaNotificacaoDTO;
import com.baseapplication.core.service.PreferenciaNotificacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/preferencias-notificacao")
@RequiredArgsConstructor
@Tag(name = "Preferências de Notificação", description = "API para gerenciar preferências de notificação de membros")
public class PreferenciaNotificacaoController {

    private final PreferenciaNotificacaoService preferenciaService;

    @PostMapping
    @Operation(summary = "Salvar ou atualizar preferências", 
               description = "Salva ou atualiza as preferências de notificação de um membro")
    public ResponseEntity<PreferenciaNotificacaoDTO> salvarOuAtualizar(@RequestBody PreferenciaNotificacaoDTO dto) {
        try {
            PreferenciaNotificacaoDTO resultado = preferenciaService.salvarOuAtualizar(dto);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao salvar preferências: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/banda/{idBanda}/usuario/{idUsuario}")
    @Operation(summary = "Buscar preferências por banda e usuário", 
               description = "Busca as preferências de notificação de um usuário em uma banda")
    public ResponseEntity<PreferenciaNotificacaoDTO> buscarPorBandaEUsuario(
            @PathVariable Long idBanda, 
            @PathVariable Long idUsuario) {
        try {
            PreferenciaNotificacaoDTO resultado = preferenciaService.buscarPorBandaEUsuario(idBanda, idUsuario);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao buscar preferências: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/banda/{idBanda}/membro-fantasma/{idMembroFantasma}")
    @Operation(summary = "Buscar preferências por banda e membro fantasma", 
               description = "Busca as preferências de notificação de um membro fantasma em uma banda")
    public ResponseEntity<PreferenciaNotificacaoDTO> buscarPorBandaEMembroFantasma(
            @PathVariable Long idBanda, 
            @PathVariable Long idMembroFantasma) {
        try {
            PreferenciaNotificacaoDTO resultado = preferenciaService.buscarPorBandaEMembroFantasma(idBanda, idMembroFantasma);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao buscar preferências: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/banda/{idBanda}")
    @Operation(summary = "Buscar todas as preferências de uma banda", 
               description = "Busca todas as preferências de notificação dos membros de uma banda")
    public ResponseEntity<List<PreferenciaNotificacaoDTO>> buscarPorBanda(@PathVariable Long idBanda) {
        try {
            List<PreferenciaNotificacaoDTO> resultado = preferenciaService.buscarPorBanda(idBanda);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao buscar preferências: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
