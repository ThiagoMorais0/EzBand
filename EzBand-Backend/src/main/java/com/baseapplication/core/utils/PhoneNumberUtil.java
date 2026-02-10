package com.baseapplication.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PhoneNumberUtil {
    
    public String normalizeToE164(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Número de telefone não pode ser vazio");
        }
        
        String cleaned = phoneNumber.replaceAll("[^0-9+]", "");
        
        if (cleaned.startsWith("+")) {
            return cleaned;
        }
        
        if (cleaned.startsWith("55")) {
            cleaned = "+" + cleaned;
        } else if (cleaned.length() >= 10 && !cleaned.startsWith("0")) {
            cleaned = "+55" + cleaned;
        } else {
            cleaned = "+" + cleaned;
        }
        
        log.debug("Número normalizado de {} para {}", phoneNumber, cleaned);
        return cleaned;
    }
    
    public boolean isValidPhoneNumber(String phoneNumber) {
        try {
            String normalized = normalizeToE164(phoneNumber);
            return normalized.matches("^\\+[1-9]\\d{1,14}$");
        } catch (Exception e) {
            return false;
        }
    }
}
