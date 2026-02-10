package com.baseapplication.core.model;

import com.baseapplication.core.dto.whatsapp.StatusEnvio;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "whatsapp_message_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappMessageLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String destino;
    
    @Column(columnDefinition = "TEXT")
    private String conteudo;
    
    @Column(nullable = false)
    private String provider;
    
    @Column(name = "message_id")
    private String messageId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEnvio status;
    
    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;
    
    @Column(columnDefinition = "TEXT")
    private String erro;
    
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;
    
    @PrePersist
    protected void onCreate() {
        criadoEm = LocalDateTime.now();
        if (dataEnvio == null) {
            dataEnvio = LocalDateTime.now();
        }
    }
}
