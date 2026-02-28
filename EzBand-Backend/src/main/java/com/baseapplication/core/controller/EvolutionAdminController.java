package com.baseapplication.core.controller;

import com.baseapplication.core.service.WhatsappService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/admin/evolution")
@RequiredArgsConstructor
public class EvolutionAdminController {
    
    private final WhatsappService whatsappService;
    
    @Value("${evolution.admin.password:admin123}")
    private String adminPassword;
    
    private static final String ADMIN_SESSION_KEY = "EVOLUTION_ADMIN_AUTHENTICATED";
    
    @GetMapping
    public String adminPanel() {
        return "evolution-admin";
    }
    
    @PostMapping("/auth/login")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials, 
                                                      HttpSession session) {
        String password = credentials.get("password");
        
        log.debug("Tentativa de login - Senha esperada length: {}, Senha recebida length: {}", 
                 adminPassword != null ? adminPassword.length() : 0, 
                 password != null ? password.length() : 0);
        
        if (password != null && password.equals(adminPassword)) {
            session.setAttribute(ADMIN_SESSION_KEY, true);
            log.info("✅ Admin autenticado com sucesso. Session ID: {}", session.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Autenticação realizada com sucesso"
            ));
        }
        
        log.warn("❌ Tentativa de login com senha incorreta. Password configured: {}", adminPassword != null && !adminPassword.isBlank());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
            "success", false,
            "message", "Senha incorreta"
        ));
    }
    
    @PostMapping("/auth/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        session.removeAttribute(ADMIN_SESSION_KEY);
        session.invalidate();
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Logout realizado com sucesso"
        ));
    }
    
    @GetMapping("/auth/check")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkAuth(HttpSession session) {
        Boolean isAuthenticated = (Boolean) session.getAttribute(ADMIN_SESSION_KEY);
        
        return ResponseEntity.ok(Map.of(
            "authenticated", isAuthenticated != null && isAuthenticated
        ));
    }
    
    @PostMapping("/instance/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createInstance(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Não autenticado"
            ));
        }
        
        try {
            Map<String, Object> result = whatsappService.criarInstancia();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao criar instância: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/instance/qrcode")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getQRCode(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Não autenticado"
            ));
        }
        
        try {
            Map<String, Object> result = whatsappService.obterQRCode();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao obter QR Code: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/instance/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getStatus(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Não autenticado"
            ));
        }
        
        try {
            Map<String, Object> result = whatsappService.obterStatusConexao();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao obter status: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/instance/delete")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteInstance(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Não autenticado"
            ));
        }
        
        try {
            Map<String, Object> result = whatsappService.deletarInstancia();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao deletar instância: {}", e.getMessage());
            
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Instância não encontrada",
                    "message", "A instância não existe ou já foi deletada"
                ));
            }
            
            return ResponseEntity.internalServerError().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/instance/logout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> logoutInstance(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "error", "Não autenticado"
            ));
        }
        
        try {
            Map<String, Object> result = whatsappService.desconectarInstancia();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Erro ao desconectar instância: {}", e.getMessage());
            
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "error", "Instância não encontrada",
                    "message", "A instância não existe"
                ));
            }
            
            return ResponseEntity.internalServerError().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
    
    private boolean isAuthenticated(HttpSession session) {
        Boolean isAuth = (Boolean) session.getAttribute(ADMIN_SESSION_KEY);
        return isAuth != null && isAuth;
    }
}
