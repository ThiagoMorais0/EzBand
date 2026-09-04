package com.baseapplication.core.config;

import com.baseapplication.core.live.InMemoryLiveSessionStore;
import com.baseapplication.core.live.LiveSessionStore;
import com.baseapplication.core.live.RedisLiveSessionStore;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

/**
 * Escolhe o store das sessões ao vivo.
 *
 * <p>A decisão é em tempo de execução, num único bean, em vez de dois {@code @Component}
 * com {@code @ConditionalOnMissingBean} — que só é confiável em auto-configuração e falha
 * silenciosamente em configuração de aplicação.
 */
@Configuration
public class LiveSessionStoreConfig {

    @Bean
    public LiveSessionStore liveSessionStore(ObjectProvider<StringRedisTemplate> redisTemplate,
                                             @Value("${spring.data.redis.host:}") String redisHost,
                                             ObjectMapper objectMapper) {
        StringRedisTemplate template = redisTemplate.getIfAvailable();
        if (StringUtils.hasText(redisHost) && template != null) {
            return new RedisLiveSessionStore(template, objectMapper);
        }
        return new InMemoryLiveSessionStore();
    }
}
