package com.baseapplication.core.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Slf4j
@Configuration
@Getter
public class EvolutionApiConfig {
    
    @Value("${evolution.api.url:http://localhost:8082}")
    private String evolutionApiUrl;
    
    @Value("${evolution.api.key:change-me}")
    private String evolutionApiKey;
    
    @Value("${evolution.instance.name:ezband-whatsapp}")
    private String instanceName;
    
    @Bean(name = "evolutionWebClient")
    public WebClient evolutionWebClient() {
        log.info("=== Evolution API Configuration ===");
        log.info("URL: {}", evolutionApiUrl);
        log.info("API Key configurada: {}", evolutionApiKey != null && !evolutionApiKey.equals("change-me") ? "✅ Sim" : "❌ NÃO (usando valor padrão)");
        log.info("API Key length: {}", evolutionApiKey.length());
        log.info("API Key preview: {}...{}", 
                evolutionApiKey.substring(0, Math.min(8, evolutionApiKey.length())),
                evolutionApiKey.length() > 10 ? evolutionApiKey.substring(evolutionApiKey.length() - 4) : "***");
        log.info("Instance Name: {}", instanceName);
        log.info("===================================");
        
        if (evolutionApiKey.equals("change-me") || evolutionApiKey.equals("change-me-to-secure-key")) {
            log.warn("⚠️ ATENÇÃO: API Key ainda está com valor padrão! Configure EVOLUTION_API_KEY no .env");
        }
        
        return WebClient.builder()
                .baseUrl(evolutionApiUrl)
                .defaultHeader("apikey", evolutionApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
