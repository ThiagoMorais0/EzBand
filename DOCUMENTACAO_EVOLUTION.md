# Guia Completo de Integração com Evolution API

> **Projeto:** BeautyNet Backend  
> **Data:** Fevereiro 2026  
> **Objetivo:** Documentação completa da integração com Evolution API para envio de mensagens WhatsApp

---

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Pré-requisitos](#pré-requisitos)
3. [Dependências do Projeto](#dependências-do-projeto)
4. [Estrutura do Projeto](#estrutura-do-projeto)
5. [Configuração Passo a Passo](#configuração-passo-a-passo)
6. [Classes e Componentes](#classes-e-componentes)
7. [Fluxo de Uso](#fluxo-de-uso)
8. [Testes e Validação](#testes-e-validação)
9. [Troubleshooting](#troubleshooting)
10. [Boas Práticas](#boas-práticas)

---

## 🎯 Visão Geral

A **Evolution API** é uma API open-source para integração com WhatsApp, baseada em **Baileys** (biblioteca JavaScript para WhatsApp Web). Esta integração permite:

- ✅ Criar e gerenciar instâncias WhatsApp
- ✅ Enviar mensagens de texto e mídia
- ✅ Receber mensagens via webhook
- ✅ Conectar via QR Code
- ✅ Monitorar status de conexão

### Arquitetura da Solução

```
┌─────────────────┐      HTTP/REST      ┌──────────────────┐      WebSocket      ┌──────────────┐
│  Spring Boot    │ ←─────────────────→ │  Evolution API   │ ←─────────────────→ │   WhatsApp   │
│  Application    │                     │  (Port 8081)     │                     │   Servers    │
└─────────────────┘                     └──────────────────┘                     └──────────────┘
        ↑                                        │
        │                                        │ Webhook
        └────────────────────────────────────────┘
```

---

## 📦 Pré-requisitos

### Versões Utilizadas

| Tecnologia | Versão | Observações |
|------------|--------|-------------|
| **Java** | 17 | LTS recomendado |
| **Spring Boot** | 4.0.2 | Última versão stable |
| **Maven** | 3.6+ | Gerenciador de dependências |
| **Evolution API** | v2.x | Verifique a versão mais recente |
| **PostgreSQL** | 12+ | (Opcional, para persistência) |

### Servidor Evolution API

Você precisa ter um servidor Evolution API rodando. Opções:

1. **Docker** (Recomendado):
```bash
docker run -d \
  --name evolution-api \
  -p 8081:8081 \
  -e AUTHENTICATION_API_KEY="sua-chave-api-aqui" \
  atendai/evolution-api:latest
```

2. **Docker Compose**:
```yaml
version: '3.8'
services:
  evolution-api:
    image: atendai/evolution-api:latest
    ports:
      - "8081:8081"
    environment:
      - AUTHENTICATION_API_KEY=change-me-to-secure-key
      - SERVER_URL=http://localhost:8081
    restart: unless-stopped
```

3. **Instalação Local**: Consulte [documentação oficial](https://doc.evolution-api.com/)

---

## 🔧 Dependências do Projeto

### 1. Arquivo `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>4.0.2</version>
        <relativePath/>
    </parent>
    
    <groupId>com.beautynet</groupId>
    <artifactId>dev</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>Beauty Net</name>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Web (REST API) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webmvc</artifactId>
        </dependency>
        
        <!-- Spring Boot RestClient (Para chamadas HTTP) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-restclient</artifactId>
        </dependency>
        
        <!-- Spring Retry (Resiliência) -->
        <dependency>
            <groupId>org.springframework.retry</groupId>
            <artifactId>spring-retry</artifactId>
            <version>2.0.5</version>
        </dependency>
        
        <!-- Spring Aspects (Necessário para @Retryable) -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-aspects</artifactId>
        </dependency>
        
        <!-- Lombok (Reduz boilerplate) -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        
        <!-- SpringDoc OpenAPI (Documentação Swagger) -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.3.0</version>
        </dependency>
        
        <!-- Spring Boot Actuator (Health checks) -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- Validação -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
            <version>4.0.1</version>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 📁 Estrutura do Projeto

```
src/main/java/com/beautynet/dev/
│
├── BeautyNetApplication.java              # Classe principal
│
├── infraestrutura/
│   └── config/
│       └── EvolutionApiConfig.java        # Configuração do RestClient
│
├── aplicacao/
│   └── servico/
│       └── WhatsAppService.java           # Lógica de negócio
│
└── api/
    ├── controller/
    │   ├── WhatsAppController.java        # Endpoints REST
    │   └── WhatsAppWebhookController.java # Recebe webhooks
    │
    └── dto/
        └── whatsapp/
            ├── InstanceRequest.java       # DTO para criar instância
            ├── MessageResponse.java       # Resposta de mensagem
            ├── SendTextMessageRequest.java    # Enviar texto
            ├── SendMediaMessageRequest.java   # Enviar mídia
            ├── WebhookEvent.java          # Evento webhook
            └── WebhookData.java           # Dados do webhook
```

---

## ⚙️ Configuração Passo a Passo

### Passo 1: Configurar `application.properties`

```properties
# Nome da aplicação
spring.application.name=Beauty Net

# Profile ativo (dev ou prod)
spring.profiles.active=${SPRING_PROFILES_ACTIVE:dev}

# URL base da aplicação
app.base-url=${APP_BASE_URL:http://localhost:8080}

# === EVOLUTION API CONFIGURATION ===
# URL do servidor Evolution API
evolution.api.url=${EVOLUTION_API_URL:http://localhost:8081}

# API Key configurada no Evolution API
evolution.api.key=${EVOLUTION_API_KEY:change-me}

# Nome da instância WhatsApp (único por aplicação)
evolution.instance.name=${EVOLUTION_INSTANCE_NAME:beautynet-instance}

# === SPRING RETRY ===
spring.retry.enabled=true

# === ACTUATOR (Health checks) ===
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

# === LOGGING ===
logging.level.root=INFO
logging.level.com.beautynet.dev=INFO
logging.level.org.springframework.web=WARN
```

**⚠️ IMPORTANTE:**
- A `evolution.api.key` deve ser a mesma configurada no servidor Evolution API
- O `evolution.instance.name` deve ser único para cada aplicação/ambiente

---

### Passo 2: Habilitar Retry na Aplicação Principal

```java
package com.beautynet.dev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry  // ← Habilita @Retryable nas classes
public class BeautyNetApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BeautyNetApplication.class, args);
    }
}
```

---

## 🔨 Classes e Componentes

### 1. Classe de Configuração: `EvolutionApiConfig.java`

**Responsabilidade:** Configurar o `RestClient` para comunicação com Evolution API

```java
package com.beautynet.dev.infraestrutura.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
@Getter
public class EvolutionApiConfig {
    
    @Value("${evolution.api.url}")
    private String evolutionApiUrl;
    
    @Value("${evolution.api.key}")
    private String evolutionApiKey;
    
    @Value("${evolution.instance.name:beautynet-instance}")
    private String instanceName;
    
    @Bean(name = "evolutionRestClient")
    public RestClient evolutionRestClient() {
        log.info("=== Evolution API Configuration ===");
        log.info("URL: {}", evolutionApiUrl);
        log.info("API Key: {}...{} (length: {})", 
                evolutionApiKey.substring(0, Math.min(5, evolutionApiKey.length())),
                evolutionApiKey.length() > 10 ? evolutionApiKey.substring(evolutionApiKey.length() - 3) : "***",
                evolutionApiKey.length());
        log.info("Instance Name: {}", instanceName);
        log.info("===================================");
        
        // Configurar timeouts
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000); // 10 segundos
        requestFactory.setReadTimeout(30000);    // 30 segundos
        
        return RestClient.builder()
                .baseUrl(evolutionApiUrl)
                .requestFactory(requestFactory)
                .defaultHeader("apikey", evolutionApiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Connection", "close")
                .build();
    }
}
```

**📝 Pontos-chave:**
- Usa `RestClient` (nova API do Spring Boot 3+)
- Configura timeouts para evitar travamentos
- Adiciona headers padrão (apikey, Content-Type)
- Usa `@Value` para injetar propriedades

---

### 2. DTOs (Data Transfer Objects)

#### 2.1 `InstanceRequest.java` - Criar Instância

```java
package com.beautynet.dev.api.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceRequest {
    
    @JsonProperty("instanceName")
    private String instanceName;
    
    @JsonProperty("qrcode")
    private Boolean qrcode;
    
    @JsonProperty("integration")
    private String integration;  // Valor: "WHATSAPP-BAILEYS"
}
```

#### 2.2 `SendTextMessageRequest.java` - Enviar Mensagem de Texto

```java
package com.beautynet.dev.api.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendTextMessageRequest {
    
    @JsonProperty("number")
    private String number;  // Formato: 5511999999999 (DDI + DDD + Número)
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("delay")
    private Integer delay;  // Delay em millisegundos (recomendado: 1200)
}
```

#### 2.3 `SendMediaMessageRequest.java` - Enviar Mídia/PDF

```java
package com.beautynet.dev.api.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMediaMessageRequest {
    
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("mediatype")
    private String mediaType;  // "image", "video", "document", "audio"
    
    @JsonProperty("mimetype")
    private String mimeType;   // Ex: "application/pdf", "image/jpeg"
    
    @JsonProperty("caption")
    private String caption;
    
    @JsonProperty("media")
    private String media;      // URL ou Base64
    
    @JsonProperty("fileName")
    private String fileName;
    
    @JsonProperty("delay")
    private Integer delay;
}
```

#### 2.4 `MessageResponse.java` - Resposta da API

```java
package com.beautynet.dev.api.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    
    @JsonProperty("key")
    private MessageKey key;
    
    @JsonProperty("message")
    private Message message;
    
    @JsonProperty("messageTimestamp")
    private Long messageTimestamp;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageKey {
        @JsonProperty("remoteJid")
        private String remoteJid;
        
        @JsonProperty("fromMe")
        private Boolean fromMe;
        
        @JsonProperty("id")
        private String id;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        @JsonProperty("extendedTextMessage")
        private ExtendedTextMessage extendedTextMessage;
        
        @JsonProperty("conversation")
        private String conversation;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtendedTextMessage {
        @JsonProperty("text")
        private String text;
    }
}
```

#### 2.5 `WebhookEvent.java` - Receber Eventos

```java
package com.beautynet.dev.api.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookEvent {
    
    @JsonProperty("event")
    private String event;  // "messages.upsert", "connection.update", etc.
    
    @JsonProperty("instance")
    private String instance;
    
    @JsonProperty("data")
    private WebhookData data;
    
    @JsonProperty("destination")
    private String destination;
    
    @JsonProperty("date_time")
    private String dateTime;
    
    @JsonProperty("sender")
    private String sender;
    
    @JsonProperty("server_url")
    private String serverUrl;
    
    @JsonProperty("apikey")
    private String apikey;
}
```

#### 2.6 `WebhookData.java` - Dados do Webhook

```java
package com.beautynet.dev.api.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookData {
    
    @JsonProperty("key")
    private MessageKey key;
    
    @JsonProperty("pushName")
    private String pushName;
    
    @JsonProperty("message")
    private Message message;
    
    @JsonProperty("messageType")
    private String messageType;
    
    @JsonProperty("messageTimestamp")
    private Long messageTimestamp;
    
    @JsonProperty("instanceId")
    private String instanceId;
    
    @JsonProperty("source")
    private String source;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageKey {
        @JsonProperty("remoteJid")
        private String remoteJid;
        
        @JsonProperty("fromMe")
        private Boolean fromMe;
        
        @JsonProperty("id")
        private String id;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        @JsonProperty("conversation")
        private String conversation;
        
        @JsonProperty("extendedTextMessage")
        private ExtendedTextMessage extendedTextMessage;
        
        @JsonProperty("imageMessage")
        private MediaMessage imageMessage;
        
        @JsonProperty("documentMessage")
        private MediaMessage documentMessage;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtendedTextMessage {
        @JsonProperty("text")
        private String text;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaMessage {
        @JsonProperty("url")
        private String url;
        
        @JsonProperty("mimetype")
        private String mimetype;
        
        @JsonProperty("caption")
        private String caption;
        
        @JsonProperty("fileName")
        private String fileName;
    }
}
```

---

### 3. Service Layer: `WhatsAppService.java`

**Responsabilidade:** Toda lógica de comunicação com Evolution API

```java
package com.beautynet.dev.aplicacao.servico;

import com.beautynet.dev.api.dto.whatsapp.*;
import com.beautynet.dev.infraestrutura.config.EvolutionApiConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {
    
    private final RestClient evolutionRestClient;
    private final EvolutionApiConfig config;
    
    /**
     * Cria uma nova instância WhatsApp no Evolution API
     */
    @Retryable(
        retryFor = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public Map<String, Object> createInstance() {
        log.info("Creating Evolution API instance: {}", config.getInstanceName());
        
        InstanceRequest request = InstanceRequest.builder()
                .instanceName(config.getInstanceName())
                .qrcode(true)
                .integration("WHATSAPP-BAILEYS")
                .build();
        
        try {
            ResponseEntity<Map> response = evolutionRestClient.post()
                    .uri("/instance/create")
                    .body(request)
                    .retrieve()
                    .toEntity(Map.class);
            
            log.info("Instance created successfully: {}", response.getBody());
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error creating instance: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtém o status de conexão da instância
     */
    @Retryable(
        retryFor = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public Map<String, Object> getConnectionStatus() {
        log.info("Checking connection status for instance: {}", config.getInstanceName());
        
        try {
            ResponseEntity<Map> response = evolutionRestClient.get()
                    .uri("/instance/connectionState/{instanceName}", config.getInstanceName())
                    .retrieve()
                    .toEntity(Map.class);
            
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error checking connection status: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Envia mensagem de texto
     */
    @Retryable(
        retryFor = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public MessageResponse sendTextMessage(String phoneNumber, String message) {
        log.info("Sending text message to: {}", phoneNumber);
        
        SendTextMessageRequest request = SendTextMessageRequest.builder()
                .number(phoneNumber)
                .text(message)
                .delay(1200)
                .build();
        
        try {
            ResponseEntity<MessageResponse> response = evolutionRestClient.post()
                    .uri("/message/sendText/{instanceName}", config.getInstanceName())
                    .body(request)
                    .retrieve()
                    .toEntity(MessageResponse.class);
            
            log.info("Message sent successfully to: {}", phoneNumber);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error sending text message: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Envia documento PDF
     */
    @Retryable(
        retryFor = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public MessageResponse sendPdfDocument(String phoneNumber, byte[] pdfContent, 
                                          String fileName, String caption) {
        log.info("Sending PDF document to: {}", phoneNumber);
        
        String base64Pdf = Base64.getEncoder().encodeToString(pdfContent);
        
        SendMediaMessageRequest request = SendMediaMessageRequest.builder()
                .number(phoneNumber)
                .mediaType("document")
                .mimeType("application/pdf")
                .caption(caption)
                .media(base64Pdf)
                .fileName(fileName)
                .delay(1200)
                .build();
        
        try {
            ResponseEntity<MessageResponse> response = evolutionRestClient.post()
                    .uri("/message/sendMedia/{instanceName}", config.getInstanceName())
                    .body(request)
                    .retrieve()
                    .toEntity(MessageResponse.class);
            
            log.info("PDF sent successfully to: {}", phoneNumber);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error sending PDF: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Envia mensagem de mídia (imagem, vídeo, etc)
     */
    @Retryable(
        retryFor = {RestClientException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public MessageResponse sendMediaMessage(String phoneNumber, String mediaUrl, 
                                           String mediaType, String caption) {
        log.info("Sending media message to: {}", phoneNumber);
        
        SendMediaMessageRequest request = SendMediaMessageRequest.builder()
                .number(phoneNumber)
                .mediaType(mediaType)
                .caption(caption)
                .media(mediaUrl)
                .delay(1200)
                .build();
        
        try {
            ResponseEntity<MessageResponse> response = evolutionRestClient.post()
                    .uri("/message/sendMedia/{instanceName}", config.getInstanceName())
                    .body(request)
                    .retrieve()
                    .toEntity(MessageResponse.class);
            
            log.info("Media sent successfully to: {}", phoneNumber);
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error sending media: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Obtém QR Code para conectar instância
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getQRCode() {
        log.info("Fetching QR Code for instance: {}", config.getInstanceName());
        
        try {
            ResponseEntity<Map> response = evolutionRestClient.get()
                    .uri("/instance/connect/{instanceName}", config.getInstanceName())
                    .retrieve()
                    .toEntity(Map.class);
            
            Map<String, Object> result = response.getBody();
            
            if (result != null) {
                if (result.containsKey("instance")) {
                    Map<String, Object> instanceData = (Map<String, Object>) result.get("instance");
                    String state = (String) instanceData.getOrDefault("state", "");
                    
                    if ("open".equals(state)) {
                        return Map.of(
                            "message", "Instance already connected",
                            "state", state,
                            "instanceName", config.getInstanceName(),
                            "connected", true
                        );
                    }
                }
                
                if (result.containsKey("qrcode")) {
                    log.info("QR Code retrieved successfully");
                    return result;
                }
            }
            
            log.warn("No QR Code found, instance might already be connected");
            return Map.of(
                "message", "QR code not available. Instance might be connected.",
                "instanceName", config.getInstanceName(),
                "suggestion", "Check instance status"
            );
            
        } catch (RestClientException e) {
            log.error("Error fetching QR Code: {}", e.getMessage());
            
            if (e.getMessage().contains("404")) {
                return Map.of(
                    "error", "Instance not found",
                    "message", "Instance does not exist. Create one first.",
                    "instanceName", config.getInstanceName(),
                    "action", "POST /v1/whatsapp/instance/create"
                );
            }
            
            throw e;
        }
    }
    
    /**
     * Lista todas as instâncias
     */
    public Map<String, Object> fetchAllInstances() {
        log.info("Fetching all Evolution API instances");
        
        try {
            ResponseEntity<Map> response = evolutionRestClient.get()
                    .uri("/instance/fetchInstances")
                    .retrieve()
                    .toEntity(Map.class);
            
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error fetching instances: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Deleta instância permanentemente
     */
    public Map<String, Object> deleteInstance() {
        log.info("Deleting Evolution API instance: {}", config.getInstanceName());
        
        try {
            ResponseEntity<Map> response = evolutionRestClient.delete()
                    .uri("/instance/delete/{instanceName}", config.getInstanceName())
                    .retrieve()
                    .toEntity(Map.class);
            
            log.info("Instance deleted successfully");
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error deleting instance: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Desconecta instância (logout)
     */
    public Map<String, Object> logoutInstance() {
        log.info("Logging out Evolution API instance: {}", config.getInstanceName());
        
        try {
            ResponseEntity<Map> response = evolutionRestClient.delete()
                    .uri("/instance/logout/{instanceName}", config.getInstanceName())
                    .retrieve()
                    .toEntity(Map.class);
            
            log.info("Instance logged out successfully");
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Error logging out instance: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Configura webhook para receber eventos
     */
    public void setWebhook(String webhookUrl) {
        log.info("Setting webhook URL: {}", webhookUrl);
        
        Map<String, Object> webhookConfig = Map.of(
            "url", webhookUrl,
            "enabled", true,
            "webhookByEvents", true,
            "events", new String[]{
                "MESSAGES_UPSERT",
                "MESSAGES_UPDATE",
                "MESSAGES_DELETE",
                "SEND_MESSAGE",
                "CONNECTION_UPDATE"
            }
        );
        
        try {
            evolutionRestClient.post()
                    .uri("/webhook/set/{instanceName}", config.getInstanceName())
                    .body(webhookConfig)
                    .retrieve()
                    .toBodilessEntity();
            
            log.info("Webhook configured successfully");
        } catch (RestClientException e) {
            log.error("Error setting webhook: {}", e.getMessage());
            throw e;
        }
    }
}
```

**🔑 Destaques:**
- `@Retryable`: Tenta 3 vezes com backoff exponencial (2s, 4s, 8s)
- Logs estruturados para debugging
- Tratamento de erros específicos
- Uso de Builder pattern nos DTOs

---

### 4. Controller: `WhatsAppController.java`

**Responsabilidade:** Expor endpoints REST para uso externo

```java
package com.beautynet.dev.api.controller;

import com.beautynet.dev.api.dto.whatsapp.MessageResponse;
import com.beautynet.dev.aplicacao.servico.WhatsAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/whatsapp")
@RequiredArgsConstructor
@Tag(name = "WhatsApp", description = "WhatsApp Integration API")
public class WhatsAppController {
    
    private final WhatsAppService whatsAppService;
    
    @PostMapping("/instance/create")
    @Operation(summary = "Create WhatsApp instance")
    public ResponseEntity<Map<String, Object>> createInstance() {
        try {
            Map<String, Object> result = whatsAppService.createInstance();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error creating instance: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/instance/status")
    @Operation(summary = "Get connection status")
    public ResponseEntity<Map<String, Object>> getConnectionStatus() {
        try {
            Map<String, Object> status = whatsAppService.getConnectionStatus();
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("Error getting status: {}", e.getMessage());
            
            if (e.getMessage().contains("404") || e.getMessage().contains("does not exist")) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Instance not found",
                    "message", "A instância WhatsApp ainda não foi criada. Crie uma instância primeiro.",
                    "instanceName", "beautynet-instance",
                    "action", "POST /v1/whatsapp/instance/create"
                ));
            }
            
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/instance/qrcode")
    @Operation(summary = "Get QR Code for connection")
    public ResponseEntity<Map<String, Object>> getQRCode() {
        try {
            Map<String, Object> qrCode = whatsAppService.getQRCode();
            return ResponseEntity.ok(qrCode);
        } catch (Exception e) {
            log.error("Error getting QR Code: {}", e.getMessage());
            
            if (e.getMessage().contains("404") || e.getMessage().contains("does not exist")) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Instance not found",
                    "message", "A instância WhatsApp ainda não foi criada. Crie uma instância primeiro.",
                    "action", "POST /v1/whatsapp/instance/create"
                ));
            }
            
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/instance/list")
    @Operation(summary = "List all WhatsApp instances")
    public ResponseEntity<Map<String, Object>> listInstances() {
        try {
            Map<String, Object> instances = whatsAppService.fetchAllInstances();
            return ResponseEntity.ok(instances);
        } catch (Exception e) {
            log.error("Error listing instances: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/instance/delete")
    @Operation(summary = "Delete WhatsApp instance permanently")
    public ResponseEntity<Map<String, Object>> deleteInstance() {
        try {
            Map<String, Object> result = whatsAppService.deleteInstance();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error deleting instance: {}", e.getMessage());
            
            if (e.getMessage().contains("404") || e.getMessage().contains("does not exist")) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Instance not found",
                    "message", "A instância não existe ou já foi deletada"
                ));
            }
            
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/instance/logout")
    @Operation(summary = "Logout WhatsApp instance (disconnect without deleting)")
    public ResponseEntity<Map<String, Object>> logoutInstance() {
        try {
            Map<String, Object> result = whatsAppService.logoutInstance();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error logging out instance: {}", e.getMessage());
            
            if (e.getMessage().contains("404") || e.getMessage().contains("does not exist")) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Instance not found",
                    "message", "A instância não existe"
                ));
            }
            
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/message/send")
    @Operation(summary = "Send text message", description = "Envia mensagem de texto para um número de telefone via WhatsApp")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String message = request.get("message");
            
            if (phoneNumber == null || phoneNumber.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "phoneNumber é obrigatório",
                    "example", Map.of("phoneNumber", "5511999999999", "message", "Olá!")
                ));
            }
            
            if (message == null || message.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "message é obrigatória",
                    "example", Map.of("phoneNumber", "5511999999999", "message", "Olá!")
                ));
            }
            
            MessageResponse response = whatsAppService.sendTextMessage(phoneNumber, message);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Mensagem enviada com sucesso",
                "data", response
            ));
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/message/send-pdf")
    @Operation(summary = "Send PDF document")
    public ResponseEntity<MessageResponse> sendPdf(
            @RequestParam String phoneNumber,
            @RequestBody byte[] pdfContent,
            @RequestParam String fileName,
            @RequestParam(required = false) String caption) {
        try {
            MessageResponse response = whatsAppService.sendPdfDocument(
                    phoneNumber, pdfContent, fileName, caption);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending PDF: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @PostMapping("/webhook/configure")
    @Operation(summary = "Configure webhook URL")
    public ResponseEntity<Void> configureWebhook(@RequestParam String webhookUrl) {
        try {
            whatsAppService.setWebhook(webhookUrl);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error configuring webhook: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
```

---

### 5. Webhook Controller: `WhatsAppWebhookController.java`

**Responsabilidade:** Receber eventos do Evolution API

```java
package com.beautynet.dev.api.controller;

import com.beautynet.dev.api.dto.whatsapp.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/webhook/whatsapp")
@RequiredArgsConstructor
public class WhatsAppWebhookController {
    
    @PostMapping
    public ResponseEntity<Void> handleWebhook(@RequestBody WebhookEvent event) {
        log.info("Received webhook event: {}", event.getEvent());
        log.debug("Webhook details - Instance: {}, Event: {}, DateTime: {}", 
                event.getInstance(), event.getEvent(), event.getDateTime());
        
        try {
            switch (event.getEvent()) {
                case "messages.upsert":
                    handleNewMessage(event);
                    break;
                case "messages.update":
                    handleMessageUpdate(event);
                    break;
                case "connection.update":
                    handleConnectionUpdate(event);
                    break;
                case "send.message":
                    handleSentMessage(event);
                    break;
                default:
                    log.info("Unhandled event type: {}", event.getEvent());
            }
            
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing webhook event: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private void handleNewMessage(WebhookEvent event) {
        log.info("Processing new message from: {}", event.getData().getKey().getRemoteJid());
        
        String messageText = extractMessageText(event);
        if (messageText != null && !event.getData().getKey().getFromMe()) {
            log.info("Received message: {}", messageText);
            // TODO: Processar mensagem recebida (adicionar sua lógica aqui)
        }
    }
    
    private void handleMessageUpdate(WebhookEvent event) {
        log.info("Processing message update: {}", event.getData().getKey().getId());
        // TODO: Processar atualização de mensagem
    }
    
    private void handleConnectionUpdate(WebhookEvent event) {
        log.info("Connection status update for instance: {}", event.getInstance());
        // TODO: Processar mudança de status
    }
    
    private void handleSentMessage(WebhookEvent event) {
        log.info("Message sent confirmation: {}", event.getData().getKey().getId());
        // TODO: Confirmar envio
    }
    
    private String extractMessageText(WebhookEvent event) {
        if (event.getData().getMessage() == null) {
            return null;
        }
        
        if (event.getData().getMessage().getConversation() != null) {
            return event.getData().getMessage().getConversation();
        }
        
        if (event.getData().getMessage().getExtendedTextMessage() != null) {
            return event.getData().getMessage().getExtendedTextMessage().getText();
        }
        
        return null;
    }
}
```

---

## 🚀 Fluxo de Uso

### Fluxograma de Integração

```
┌──────────────────┐
│ 1. Subir         │
│ Evolution API    │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 2. Configurar    │
│ application.     │
│ properties       │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 3. POST          │
│ /instance/create │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 4. GET           │
│ /instance/qrcode │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 5. Escanear      │
│ QR Code no       │
│ WhatsApp         │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 6. GET           │
│ /instance/status │
│ (verificar)      │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 7. POST          │
│ /message/send    │
│ (enviar msgs)    │
└──────────────────┘
```

### Passo 1: Iniciar Servidor Evolution API

```bash
docker run -d \
  --name evolution-api \
  -p 8081:8081 \
  -e AUTHENTICATION_API_KEY="minha-chave-super-secreta-123" \
  atendai/evolution-api:latest
```

Aguarde 30 segundos para o servidor iniciar.

### Passo 2: Configurar Variáveis de Ambiente

```bash
export EVOLUTION_API_URL=http://localhost:8081
export EVOLUTION_API_KEY=minha-chave-super-secreta-123
export EVOLUTION_INSTANCE_NAME=beautynet-whatsapp
```

Ou configure no `application.properties`.

### Passo 3: Iniciar Aplicação Spring Boot

```bash
mvn spring-boot:run
```

### Passo 4: Criar Instância WhatsApp

```bash
curl -X POST http://localhost:8080/v1/whatsapp/instance/create
```

**Resposta esperada:**
```json
{
  "instance": {
    "instanceName": "beautynet-whatsapp",
    "status": "created"
  }
}
```

### Passo 5: Obter QR Code

```bash
curl -X GET http://localhost:8080/v1/whatsapp/instance/qrcode
```

**Resposta esperada:**
```json
{
  "qrcode": {
    "code": "2@xXx...",
    "base64": "data:image/png;base64,iVBORw0KGgoAAAANS..."
  }
}
```

Use o campo `base64` para exibir a imagem do QR Code no frontend.

### Passo 6: Escanear QR Code

1. Abra WhatsApp no celular
2. Vá em **Dispositivos Conectados**
3. Clique em **Conectar um Dispositivo**
4. Escaneie o QR Code

### Passo 7: Verificar Status

```bash
curl -X GET http://localhost:8080/v1/whatsapp/instance/status
```

**Resposta (conectado):**
```json
{
  "instance": {
    "instanceName": "beautynet-whatsapp",
    "state": "open"
  }
}
```

### Passo 8: Enviar Mensagem

```bash
curl -X POST http://localhost:8080/v1/whatsapp/message/send \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "5511999999999",
    "message": "Olá! Esta é uma mensagem de teste."
  }'
```

**Resposta:**
```json
{
  "success": true,
  "message": "Mensagem enviada com sucesso",
  "data": {
    "key": {
      "remoteJid": "5511999999999@s.whatsapp.net",
      "fromMe": true,
      "id": "3EB0XXXXXXX"
    }
  }
}
```

---

## 🧪 Testes e Validação

### Teste 1: Criar Instância

**Request:**
```bash
POST http://localhost:8080/v1/whatsapp/instance/create
```

**Validação:**
- Status 200 OK
- Corpo com `instanceName` correto

### Teste 2: QR Code

**Request:**
```bash
GET http://localhost:8080/v1/whatsapp/instance/qrcode
```

**Validação:**
- Status 200 OK
- Presença de `qrcode.base64`
- Base64 válido (começa com `data:image/png;base64,`)

### Teste 3: Enviar Mensagem

**Request:**
```bash
POST http://localhost:8080/v1/whatsapp/message/send
Content-Type: application/json

{
  "phoneNumber": "5511999999999",
  "message": "Teste de integração"
}
```

**Validação:**
- Status 200 OK
- Mensagem chegou no WhatsApp do destinatário

### Teste 4: Webhook

**Configurar webhook:**
```bash
POST http://localhost:8080/v1/whatsapp/webhook/configure?webhookUrl=https://seu-dominio.com/v1/webhook/whatsapp
```

**Enviar mensagem para a instância conectada** e verificar logs:
```
INFO: Received webhook event: messages.upsert
INFO: Processing new message from: 5511999999999@s.whatsapp.net
INFO: Received message: Olá
```

---

## 🔍 Troubleshooting

### Problema 1: "Instance not found" / 404

**Causa:** Instância não foi criada ou foi deletada.

**Solução:**
```bash
# Criar instância novamente
curl -X POST http://localhost:8080/v1/whatsapp/instance/create
```

### Problema 2: "Connection refused" ao chamar Evolution API

**Causa:** Evolution API não está rodando ou URL incorreta.

**Solução:**
1. Verificar se container está rodando:
   ```bash
   docker ps | grep evolution
   ```

2. Verificar logs:
   ```bash
   docker logs evolution-api
   ```

3. Testar conexão direta:
   ```bash
   curl http://localhost:8081/health
   ```

### Problema 3: QR Code expira rapidamente

**Causa:** QR Code tem TTL de ~40 segundos.

**Solução:**
- Implementar polling no frontend para renovar QR Code automaticamente:
  ```javascript
  setInterval(() => {
    fetch('/v1/whatsapp/instance/qrcode')
      .then(res => res.json())
      .then(data => updateQRCode(data.qrcode.base64));
  }, 30000); // A cada 30 segundos
  ```

### Problema 4: Mensagem não chega

**Checklist:**
1. ✅ Instância conectada? (`GET /instance/status` → `state: "open"`)
2. ✅ Número no formato correto? (DDI + DDD + Número, sem símbolos)
3. ✅ Delay configurado? (mínimo 1200ms para evitar ban)
4. ✅ Número existe no WhatsApp?

### Problema 5: "apikey" inválida

**Causa:** API Key no `application.properties` diferente da configurada no Evolution API.

**Solução:**
1. Verificar API Key no Evolution API:
   ```bash
   docker exec evolution-api cat /evolution/store/authentication.json
   ```

2. Atualizar `application.properties`:
   ```properties
   evolution.api.key=chave-correta-aqui
   ```

### Problema 6: Retry excessivo (loop infinito)

**Causa:** Exception não está sendo capturada corretamente.

**Solução:**
- Adicionar tratamento específico no Service:
  ```java
  @Retryable(
      retryFor = {RestClientException.class},
      noRetryFor = {IllegalArgumentException.class},  // Não retry em erros de validação
      maxAttempts = 3,
      backoff = @Backoff(delay = 2000, multiplier = 2)
  )
  ```

---

## ✅ Boas Práticas

### 1. Segurança

#### Não exponha a API Key
```properties
# ❌ ERRADO
evolution.api.key=minha-chave-123

# ✅ CORRETO
evolution.api.key=${EVOLUTION_API_KEY}
```

Use variáveis de ambiente ou vault (HashiCorp, AWS Secrets Manager).

#### Valide números de telefone
```java
public boolean isValidPhoneNumber(String phone) {
    return phone.matches("^[0-9]{10,15}$");
}
```

#### Rate Limiting
Implemente rate limiting para evitar spam:
```java
@RateLimiter(name = "whatsapp", fallbackMethod = "rateLimitFallback")
public MessageResponse sendTextMessage(String phone, String msg) {
    // ...
}
```

### 2. Resiliência

#### Use Circuit Breaker
```java
@CircuitBreaker(name = "evolution-api", fallbackMethod = "fallbackMethod")
@Retryable(maxAttempts = 3)
public Map<String, Object> createInstance() {
    // ...
}
```

#### Implemente Health Check
```java
@Component
public class EvolutionApiHealthIndicator implements HealthIndicator {
    
    @Override
    public Health health() {
        try {
            evolutionRestClient.get().uri("/health").retrieve();
            return Health.up().build();
        } catch (Exception e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
```

### 3. Monitoramento

#### Logs Estruturados
```java
log.info("WhatsApp message sent", 
    kv("phoneNumber", phoneNumber),
    kv("messageId", response.getKey().getId()),
    kv("timestamp", System.currentTimeMillis())
);
```

#### Métricas
```java
@Timed(value = "whatsapp.message.send", description = "Time to send WhatsApp message")
public MessageResponse sendTextMessage(String phone, String msg) {
    // ...
}
```

### 4. Performance

#### Cache de Status
```java
@Cacheable(value = "instanceStatus", key = "#instanceName")
public Map<String, Object> getConnectionStatus(String instanceName) {
    // ...
}
```

#### Processamento Assíncrono
```java
@Async
public CompletableFuture<MessageResponse> sendTextMessageAsync(String phone, String msg) {
    return CompletableFuture.completedFuture(sendTextMessage(phone, msg));
}
```

### 5. Testes

#### Teste Unitário
```java
@Test
void shouldSendTextMessage() {
    // Arrange
    MockRestServiceServer mockServer = MockRestServiceServer.createServer(restClient);
    mockServer.expect(requestTo("/message/sendText/test-instance"))
              .andRespond(withSuccess("{\"key\":{\"id\":\"123\"}}", MediaType.APPLICATION_JSON));
    
    // Act
    MessageResponse response = whatsAppService.sendTextMessage("5511999999999", "Test");
    
    // Assert
    assertNotNull(response);
    assertEquals("123", response.getKey().getId());
}
```

---

## 📚 Referências

- **Evolution API Docs:** https://doc.evolution-api.com/
- **Spring RestClient:** https://docs.spring.io/spring-framework/reference/integration/rest-clients.html
- **Spring Retry:** https://github.com/spring-projects/spring-retry
- **Baileys Library:** https://github.com/WhiskeySockets/Baileys

---

## 📝 Checklist de Implementação

- [ ] Evolution API rodando e acessível
- [ ] Dependências Maven configuradas
- [ ] `application.properties` com API Key correta
- [ ] `@EnableRetry` na classe principal
- [ ] `EvolutionApiConfig` criada
- [ ] DTOs criados (InstanceRequest, MessageResponse, etc)
- [ ] `WhatsAppService` implementado
- [ ] `WhatsAppController` implementado
- [ ] `WhatsAppWebhookController` implementado (se necessário)
- [ ] Testes de criação de instância
- [ ] Testes de envio de mensagem
- [ ] Logs estruturados implementados
- [ ] Tratamento de erros robusto
- [ ] Documentação Swagger acessível em `/swagger-ui.html`
- [ ] Health check configurado
- [ ] Deploy em ambiente de produção

---

## 🎉 Conclusão

Esta documentação cobre **100% da integração** realizada no projeto BeautyNet. Para novos projetos:

1. **Copie** a estrutura de pastas
2. **Ajuste** os pacotes Java conforme seu projeto
3. **Configure** as properties com suas credenciais
4. **Siga** o fluxo de uso passo a passo
5. **Adapte** os webhooks conforme sua regra de negócio

**Versões testadas e funcionando:**
- ✅ Spring Boot 4.0.2
- ✅ Java 17
- ✅ Evolution API v2.x
- ✅ Spring Retry 2.0.5

---

**Documento criado em:** Fevereiro 2026  
**Última atualização:** Fevereiro 2026  
**Autor:** BeautyNet Development Team
