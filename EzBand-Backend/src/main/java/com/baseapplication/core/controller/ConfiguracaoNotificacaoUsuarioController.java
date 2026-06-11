package com.baseapplication.core.controller;

import com.baseapplication.core.dto.ConfiguracaoNotificacaoUsuarioDTO;
import com.baseapplication.core.service.ConfiguracaoNotificacaoUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/configuracoes-notificacao")
@RequiredArgsConstructor
@Tag(name = "Configurações de Notificação", description = "Preferências globais de notificação do usuário")
public class ConfiguracaoNotificacaoUsuarioController {

    private final ConfiguracaoNotificacaoUsuarioService service;

    @GetMapping
    @Operation(summary = "Buscar configurações do usuário logado")
    public ResponseEntity<ConfiguracaoNotificacaoUsuarioDTO> buscar() {
        try {
            return ResponseEntity.ok(service.buscarParaUsuarioLogado());
        } catch (Exception e) {
            log.error("Erro ao buscar configurações de notificação: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping
    @Operation(summary = "Salvar ou atualizar configurações do usuário logado")
    public ResponseEntity<ConfiguracaoNotificacaoUsuarioDTO> salvar(@RequestBody ConfiguracaoNotificacaoUsuarioDTO dto) {
        try {
            return ResponseEntity.ok(service.salvarOuAtualizar(dto));
        } catch (Exception e) {
            log.error("Erro ao salvar configurações de notificação: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
