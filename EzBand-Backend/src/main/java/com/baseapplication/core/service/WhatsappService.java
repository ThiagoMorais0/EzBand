package com.baseapplication.core.service;

import com.baseapplication.core.dto.whatsapp.MessageResponse;
import com.baseapplication.core.dto.whatsapp.StatusEnvio;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface WhatsappService {
    
    CompletableFuture<MessageResponse> enviarMensagem(String numero, String conteudo);
    
    CompletableFuture<MessageResponse> enviarMensagemComMidia(String numero, String mediaUrl, String mediaType, String caption);
    
    StatusEnvio verificarStatus(String messageId);
    
    Map<String, Object> criarInstancia();
    
    Map<String, Object> obterStatusConexao();
    
    Map<String, Object> obterQRCode();
}
