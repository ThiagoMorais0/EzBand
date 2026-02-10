package com.baseapplication.core.service.impl;

import com.baseapplication.core.config.EvolutionApiConfig;
import com.baseapplication.core.dto.whatsapp.*;
import com.baseapplication.core.model.WhatsappMessageLog;
import com.baseapplication.core.dao.WhatsappMessageLogRepository;
import com.baseapplication.core.service.WhatsappService;
import com.baseapplication.core.utils.PhoneNumberUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvolutionApiServiceImpl implements WhatsappService {
    
    private final WebClient evolutionWebClient;
    private final EvolutionApiConfig config;
    private final WhatsappMessageLogRepository messageLogRepository;
    private final PhoneNumberUtil phoneNumberUtil;
    
    @Async
    @Override
    @Retryable(
        retryFor = {WebClientResponseException.class, Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public CompletableFuture<MessageResponse> enviarMensagem(String numero, String conteudo) {
        log.info("Enviando mensagem de texto para: {}", numero);
        
        String numeroNormalizado = phoneNumberUtil.normalizeToE164(numero);
        
        SendTextMessageRequest request = SendTextMessageRequest.builder()
                .number(numeroNormalizado)
                .text(conteudo)
                .delay(1200)
                .build();
        
        try {
            MessageResponse response = evolutionWebClient.post()
                    .uri("/message/sendText/{instanceName}", config.getInstanceName())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MessageResponse.class)
                    .block();
            
            salvarLog(numeroNormalizado, conteudo, "EVOLUTION_API", response);
            
            log.info("Mensagem enviada com sucesso para: {}", numero);
            return CompletableFuture.completedFuture(response);
        } catch (WebClientResponseException e) {
            log.error("Erro ao enviar mensagem de texto: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            salvarLogFalha(numeroNormalizado, conteudo, "EVOLUTION_API", e.getMessage());
            throw e;
        }
    }
    
    @Async
    @Override
    @Retryable(
        retryFor = {WebClientResponseException.class, Exception.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public CompletableFuture<MessageResponse> enviarMensagemComMidia(String numero, String mediaUrl, 
                                                                      String mediaType, String caption) {
        log.info("Enviando mensagem de mídia para: {}", numero);
        
        String numeroNormalizado = phoneNumberUtil.normalizeToE164(numero);
        
        SendMediaMessageRequest request = SendMediaMessageRequest.builder()
                .number(numeroNormalizado)
                .mediaType(mediaType)
                .caption(caption)
                .media(mediaUrl)
                .delay(1200)
                .build();
        
        try {
            MessageResponse response = evolutionWebClient.post()
                    .uri("/message/sendMedia/{instanceName}", config.getInstanceName())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(MessageResponse.class)
                    .block();
            
            salvarLog(numeroNormalizado, caption != null ? caption : "Mídia", "EVOLUTION_API", response);
            
            log.info("Mídia enviada com sucesso para: {}", numero);
            return CompletableFuture.completedFuture(response);
        } catch (WebClientResponseException e) {
            log.error("Erro ao enviar mídia: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            salvarLogFalha(numeroNormalizado, caption, "EVOLUTION_API", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public StatusEnvio verificarStatus(String messageId) {
        return StatusEnvio.ENVIADO;
    }
    
    @Override
    @Retryable(
        retryFor = {WebClientResponseException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public Map<String, Object> criarInstancia() {
        log.info("Criando instância Evolution API: {}", config.getInstanceName());
        
        InstanceRequest request = InstanceRequest.builder()
                .instanceName(config.getInstanceName())
                .qrcode(true)
                .integration("WHATSAPP-BAILEYS")
                .build();
        
        try {
            Map<String, Object> response = evolutionWebClient.post()
                    .uri("/instance/create")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            log.info("Instância criada com sucesso: {}", response);
            return response;
        } catch (WebClientResponseException e) {
            log.error("Erro ao criar instância: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }
    
    @Override
    @Retryable(
        retryFor = {WebClientResponseException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public Map<String, Object> obterStatusConexao() {
        log.info("Verificando status de conexão para instância: {}", config.getInstanceName());
        
        try {
            Map<String, Object> response = evolutionWebClient.get()
                    .uri("/instance/connectionState/{instanceName}", config.getInstanceName())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            return response;
        } catch (WebClientResponseException e) {
            log.error("Erro ao verificar status de conexão: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }
    
    @Override
    public Map<String, Object> obterQRCode() {
        log.info("Obtendo QR Code para instância: {}", config.getInstanceName());
        
        try {
            Map<String, Object> response = evolutionWebClient.get()
                    .uri("/instance/connect/{instanceName}", config.getInstanceName())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response != null && response.containsKey("instance")) {
                Map<String, Object> instanceData = (Map<String, Object>) response.get("instance");
                String state = (String) instanceData.getOrDefault("state", "");
                
                if ("open".equals(state)) {
                    return Map.of(
                        "message", "Instância já conectada",
                        "state", state,
                        "instanceName", config.getInstanceName(),
                        "connected", true
                    );
                }
            }
            
            log.info("QR Code obtido com sucesso");
            return response;
        } catch (WebClientResponseException e) {
            log.error("Erro ao obter QR Code: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            
            if (e.getStatusCode().value() == 404) {
                return Map.of(
                    "error", "Instância não encontrada",
                    "message", "A instância não existe. Crie uma primeiro.",
                    "instanceName", config.getInstanceName(),
                    "action", "POST /api/v1/whatsapp/instancia/criar"
                );
            }
            
            throw e;
        }
    }
    
    private void salvarLog(String destino, String conteudo, String provider, MessageResponse response) {
        try {
            WhatsappMessageLog log = new WhatsappMessageLog();
            log.setDestino(destino);
            log.setConteudo(conteudo);
            log.setProvider(provider);
            log.setMessageId(response.getKey() != null ? response.getKey().getId() : null);
            log.setStatus(StatusEnvio.ENVIADO);
            log.setDataEnvio(LocalDateTime.now());
            
            messageLogRepository.save(log);
        } catch (Exception e) {
            log.error("Erro ao salvar log de mensagem: {}", e.getMessage());
        }
    }
    
    private void salvarLogFalha(String destino, String conteudo, String provider, String erro) {
        try {
            WhatsappMessageLog log = new WhatsappMessageLog();
            log.setDestino(destino);
            log.setConteudo(conteudo);
            log.setProvider(provider);
            log.setStatus(StatusEnvio.FALHA);
            log.setDataEnvio(LocalDateTime.now());
            log.setErro(erro);
            
            messageLogRepository.save(log);
        } catch (Exception e) {
            log.error("Erro ao salvar log de falha: {}", e.getMessage());
        }
    }
}
