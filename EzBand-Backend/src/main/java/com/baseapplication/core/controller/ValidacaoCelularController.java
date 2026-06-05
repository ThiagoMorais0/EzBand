package com.baseapplication.core.controller;

import com.baseapplication.core.dto.ConfirmacaoTokenDTO;
import com.baseapplication.core.dto.ValidacaoCelularRequestDTO;
import com.baseapplication.core.service.ValidacaoCelularService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/validacao-celular")
@RequiredArgsConstructor
@Tag(name = "Validação de Celular", description = "API para validação de números de celular via WhatsApp")
public class ValidacaoCelularController {

    private final ValidacaoCelularService validacaoCelularService;

    @PostMapping("/gerar-token")
    @Operation(summary = "Gerar e enviar token de validação", 
               description = "Gera um token de 6 dígitos e envia via WhatsApp para validação do número")
    public ResponseEntity<Map<String, Object>> gerarToken(@RequestBody ValidacaoCelularRequestDTO request) {
        try {
            Map<String, Object> resultado = validacaoCelularService.gerarEEnviarToken(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao gerar token: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "erro", e.getMessage()
            ));
        }
    }

    @PostMapping("/validar-token")
    @Operation(summary = "Validar token", 
               description = "Valida o token enviado via WhatsApp e marca o celular como validado")
    public ResponseEntity<Map<String, Object>> validarToken(@RequestBody ConfirmacaoTokenDTO request) {
        try {
            Map<String, Object> resultado = validacaoCelularService.validarToken(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao validar token: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "erro", e.getMessage()
            ));
        }
    }
}
