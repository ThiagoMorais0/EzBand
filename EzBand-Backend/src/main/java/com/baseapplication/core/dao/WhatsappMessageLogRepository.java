package com.baseapplication.core.dao;

import com.baseapplication.core.dto.whatsapp.StatusEnvio;
import com.baseapplication.core.model.WhatsappMessageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WhatsappMessageLogRepository extends JpaRepository<WhatsappMessageLog, Long> {
    
    Optional<WhatsappMessageLog> findByMessageId(String messageId);
    
    List<WhatsappMessageLog> findByDestino(String destino);
    
    List<WhatsappMessageLog> findByStatus(StatusEnvio status);
    
    List<WhatsappMessageLog> findByProvider(String provider);
    
    List<WhatsappMessageLog> findByDataEnvioBetween(LocalDateTime inicio, LocalDateTime fim);
}
