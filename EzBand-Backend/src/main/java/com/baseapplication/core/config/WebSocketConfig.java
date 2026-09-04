package com.baseapplication.core.config;

import com.baseapplication.core.live.LiveSessionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * Registra o endpoint do Modo Performance.
 *
 * <p>Fica no próprio backend em vez de num serviço à parte por três razões práticas: o
 * {@code spring-boot-starter-websocket} já estava no pom, o bloco {@code /api/} do nginx já
 * repassa {@code Upgrade}/{@code Connection}, e sendo mesma origem o cookie {@code jwt}
 * httpOnly autentica o handshake sozinho — sem token na URL e sem duplicar a checagem de
 * quem participa do evento.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LiveSessionHandler liveSessionHandler;
    private final LiveSessionHandshakeInterceptor handshakeInterceptor;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public WebSocketConfig(LiveSessionHandler liveSessionHandler,
                           LiveSessionHandshakeInterceptor handshakeInterceptor) {
        this.liveSessionHandler = liveSessionHandler;
        this.handshakeInterceptor = handshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(liveSessionHandler, "/ws/live/*/*")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins(frontendUrl);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        // Mensagens do protocolo são pequenas (índices e ids); 64KB é folga generosa.
        container.setMaxTextMessageBufferSize(64 * 1024);
        container.setMaxBinaryMessageBufferSize(8 * 1024);
        // Bem acima do ping de 25s: quem passa disso sem responder está mesmo fora do ar.
        container.setMaxSessionIdleTimeout(120_000L);
        return container;
    }
}
