package com.baseapplication.core.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefixo para canais de envio de mensagens do servidor para o cliente
        config.enableSimpleBroker("/topic");
        // Prefixo para mensagens do cliente para o servidor (não usado aqui, mas padrão)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint que o frontend vai se conectar
        registry.addEndpoint("/ws-notificacoes")
                .setAllowedOriginPatterns("*");
//                .withSockJS(); // fallback para browsers antigos
    }
}

