package com.baseapplication.core.controller;

import com.baseapplication.core.dao.MembroFantasmaDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.model.MembroFantasma;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.WhatsappService;
import com.baseapplication.core.utils.PhoneNumberUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/teste-whatsapp")
@RequiredArgsConstructor
@Tag(name = "Teste WhatsApp", description = "API para testar envio de mensagens WhatsApp para membros")
public class TesteWhatsappController {

    private final WhatsappService whatsappService;
    private final MembroFantasmaDao membroFantasmaDao;
    private final UsuarioDao usuarioDao;
    private final PhoneNumberUtil phoneNumberUtil;

    @PostMapping("/membro-fantasma/{id}")
    @Operation(summary = "Enviar mensagem de teste para membro fantasma", 
               description = "Envia uma mensagem de teste via WhatsApp para um membro fantasma")
    public ResponseEntity<Map<String, Object>> enviarParaMembroFantasma(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            MembroFantasma membro = membroFantasmaDao.findById(id)
                    .orElseThrow(() -> new RuntimeException("Membro fantasma não encontrado"));
            
            if (membro.getCelular() == null || membro.getCelular().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "erro", "Membro fantasma não possui celular cadastrado"
                ));
            }
            
            if (!Boolean.TRUE.equals(membro.getCelularValidado())) {
                return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "erro", "Celular do membro fantasma não foi validado",
                    "dica", "Valide o celular antes de enviar mensagens"
                ));
            }
            
            String mensagem = request != null && request.containsKey("mensagem") 
                ? request.get("mensagem") 
                : "🎵 Olá, " + membro.getNome() + "! Esta é uma mensagem de teste do EzBand Manager.";
            
            String celularNormalizado = phoneNumberUtil.normalizeToE164(membro.getCelular());
            
            whatsappService.enviarMensagem(celularNormalizado, mensagem);
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Mensagem enviada com sucesso",
                "destinatario", membro.getNome(),
                "celular", membro.getCelular(),
                "conteudo", mensagem
            ));
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem de teste: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "erro", e.getMessage()
            ));
        }
    }

    @PostMapping("/usuario/{id}")
    @Operation(summary = "Enviar mensagem de teste para usuário", 
               description = "Envia uma mensagem de teste via WhatsApp para um usuário")
    public ResponseEntity<Map<String, Object>> enviarParaUsuario(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {
        try {
            Usuario usuario = usuarioDao.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            
            if (usuario.getCelular() == null || usuario.getCelular().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "erro", "Usuário não possui celular cadastrado"
                ));
            }
            
            String mensagem = request != null && request.containsKey("mensagem") 
                ? request.get("mensagem") 
                : "🎵 Olá, " + usuario.getNome() + "! Esta é uma mensagem de teste do EzBand Manager.";
            
            String celularNormalizado = phoneNumberUtil.normalizeToE164(usuario.getCelular());
            
            whatsappService.enviarMensagem(celularNormalizado, mensagem);
            
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Mensagem enviada com sucesso",
                "destinatario", usuario.getNome(),
                "celular", usuario.getCelular(),
                "conteudo", mensagem
            ));
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem de teste: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "erro", e.getMessage()
            ));
        }
    }
}
