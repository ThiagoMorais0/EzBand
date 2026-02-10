package com.baseapplication.core.controller;

import com.baseapplication.core.dto.whatsapp.MessageResponse;
import com.baseapplication.core.service.WhatsappService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/whatsapp")
@RequiredArgsConstructor
@Tag(name = "WhatsApp", description = "API de integração com WhatsApp via Evolution API")
public class WhatsappController {
    
    private final WhatsappService whatsappService;
    
    @PostMapping("/instancia/criar")
    @Operation(summary = "Criar instância WhatsApp", description = "Cria uma nova instância WhatsApp no Evolution API")
    public ResponseEntity<Map<String, Object>> criarInstancia() {
        try {
            Map<String, Object> resultado = whatsappService.criarInstancia();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error("Erro ao criar instância: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", e.getMessage()));
        }
    }
    
    @GetMapping("/instancia/status")
    @Operation(summary = "Obter status de conexão", description = "Verifica o status de conexão da instância WhatsApp")
    public ResponseEntity<Map<String, Object>> obterStatusConexao() {
        try {
            Map<String, Object> status = whatsappService.obterStatusConexao();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Erro ao obter status: {}", e.getMessage());
            
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return ResponseEntity.status(404).body(Map.of(
                    "erro", "Instância não encontrada",
                    "mensagem", "A instância WhatsApp ainda não foi criada. Crie uma instância primeiro.",
                    "acao", "POST /api/v1/whatsapp/instancia/criar"
                ));
            }
            
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", e.getMessage()));
        }
    }
    
    @GetMapping("/instancia/qrcode")
    @Operation(summary = "Obter QR Code", description = "Obtém o QR Code para conectar a instância WhatsApp")
    public ResponseEntity<Map<String, Object>> obterQRCode() {
        try {
            Map<String, Object> qrCode = whatsappService.obterQRCode();
            return ResponseEntity.ok(qrCode);
        } catch (Exception e) {
            log.error("Erro ao obter QR Code: {}", e.getMessage());
            
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return ResponseEntity.status(404).body(Map.of(
                    "erro", "Instância não encontrada",
                    "mensagem", "A instância WhatsApp ainda não foi criada. Crie uma instância primeiro.",
                    "acao", "POST /api/v1/whatsapp/instancia/criar"
                ));
            }
            
            return ResponseEntity.internalServerError()
                    .body(Map.of("erro", e.getMessage()));
        }
    }
    
    @PostMapping("/mensagem/enviar")
    @Operation(summary = "Enviar mensagem de texto", 
               description = "Envia uma mensagem de texto via WhatsApp de forma assíncrona")
    public ResponseEntity<Map<String, Object>> enviarMensagem(@RequestBody Map<String, String> request) {
        try {
            String numero = request.get("numero");
            String mensagem = request.get("mensagem");
            
            if (numero == null || numero.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Número é obrigatório",
                    "exemplo", Map.of("numero", "5511999999999", "mensagem", "Olá!")
                ));
            }
            
            if (mensagem == null || mensagem.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Mensagem é obrigatória",
                    "exemplo", Map.of("numero", "5511999999999", "mensagem", "Olá!")
                ));
            }
            
            CompletableFuture<MessageResponse> future = whatsappService.enviarMensagem(numero, mensagem);
            
            return ResponseEntity.accepted().body(Map.of(
                "sucesso", true,
                "mensagem", "Mensagem está sendo enviada de forma assíncrona",
                "numero", numero
            ));
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "sucesso", false,
                "erro", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/mensagem/enviar-midia")
    @Operation(summary = "Enviar mensagem com mídia", 
               description = "Envia uma mensagem com mídia (imagem, vídeo, documento) via WhatsApp")
    public ResponseEntity<Map<String, Object>> enviarMensagemComMidia(@RequestBody Map<String, String> request) {
        try {
            String numero = request.get("numero");
            String mediaUrl = request.get("mediaUrl");
            String mediaType = request.get("mediaType");
            String caption = request.get("caption");
            
            if (numero == null || numero.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Número é obrigatório"
                ));
            }
            
            if (mediaUrl == null || mediaUrl.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "erro", "URL da mídia é obrigatória"
                ));
            }
            
            if (mediaType == null || mediaType.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Tipo de mídia é obrigatório (image, video, document, audio)"
                ));
            }
            
            CompletableFuture<MessageResponse> future = whatsappService.enviarMensagemComMidia(
                    numero, mediaUrl, mediaType, caption);
            
            return ResponseEntity.accepted().body(Map.of(
                "sucesso", true,
                "mensagem", "Mídia está sendo enviada de forma assíncrona",
                "numero", numero
            ));
        } catch (Exception e) {
            log.error("Erro ao enviar mídia: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "sucesso", false,
                "erro", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/teste")
    @Operation(summary = "Endpoint de teste", 
               description = "Envia uma mensagem de teste para validar a integração")
    public ResponseEntity<Map<String, Object>> testeEnvio(@RequestBody Map<String, String> request) {
        String numero = request.getOrDefault("numero", "5511999999999");
        String mensagem = request.getOrDefault("mensagem", "🎵 Teste de integração WhatsApp - EzBand Manager");
        
        try {
            log.info("=== TESTE DE INTEGRAÇÃO WHATSAPP ===");
            log.info("Enviando mensagem de teste para: {}", numero);
            
            CompletableFuture<MessageResponse> future = whatsappService.enviarMensagem(numero, mensagem);
            
            return ResponseEntity.accepted().body(Map.of(
                "sucesso", true,
                "mensagem", "Mensagem de teste enviada com sucesso (assíncrona)",
                "numero", numero,
                "conteudo", mensagem,
                "info", "Verifique os logs para confirmar o envio"
            ));
        } catch (Exception e) {
            log.error("Erro no teste de envio: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "sucesso", false,
                "erro", e.getMessage(),
                "dica", "Verifique se a instância está conectada: GET /api/v1/whatsapp/instancia/status"
            ));
        }
    }
}
