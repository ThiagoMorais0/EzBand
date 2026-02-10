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
    
    @Value("${evolution.api.url:http://localhost:8081}")
    private String evolutionApiUrl;
    
    @Value("${evolution.api.key:change-me}")
    private String evolutionApiKey;
    
    @Value("${evolution.instance.name:ezband-whatsapp}")
    private String instanceName;
    
    @Bean(name = "evolutionWebClient")
    public WebClient evolutionWebClient() {
        log.info("=== Evolution API Configuration ===");
        log.info("URL: {}", evolutionApiUrl);
        log.info("API Key: {}...{} (length: {})", 
                evolutionApiKey.substring(0, Math.min(5, evolutionApiKey.length())),
                evolutionApiKey.length() > 10 ? evolutionApiKey.substring(evolutionApiKey.length() - 3) : "***",
                evolutionApiKey.length());
        log.info("Instance Name: {}", instanceName);
        log.info("===================================");
        
        return WebClient.builder()
                .baseUrl(evolutionApiUrl)
                .defaultHeader("apikey", evolutionApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
