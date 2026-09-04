package com.baseapplication.core.live;

import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Store de sessões ao vivo sobre Redis.
 *
 * <pre>
 * live:SHOW:123        → snapshot em JSON, TTL de 8h renovado a cada mutação
 * live:SHOW:123:seq    → contador INCR, mesmo TTL
 * </pre>
 *
 * <p>Redis fora do ar não pode derrubar o show: leitura e escrita degradam para
 * {@code null}/no-op logadas em warn, e o cliente continua com o último estado que já
 * tinha na tela.
 */
@Slf4j
public class RedisLiveSessionStore implements LiveSessionStore {

    private static final String PREFIXO = "live:";
    private static final String SUFIXO_SEQ = ":seq";

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper;

    public RedisLiveSessionStore(StringRedisTemplate redis, ObjectMapper mapper) {
        this.redis = redis;
        this.mapper = mapper;
        log.info("Modo Performance: usando store no Redis");
    }

    private String chave(String roomKey) {
        return PREFIXO + roomKey;
    }

    private String chaveSeq(String roomKey) {
        return PREFIXO + roomKey + SUFIXO_SEQ;
    }

    @Override
    public LiveSessionSnapshot buscar(String roomKey) {
        try {
            String json = redis.opsForValue().get(chave(roomKey));
            return json == null ? null : mapper.readValue(json, LiveSessionSnapshot.class);
        } catch (Exception e) {
            log.warn("Falha ao ler sessão ao vivo {} do Redis: {}", roomKey, e.getMessage());
            return null;
        }
    }

    @Override
    public void salvar(String roomKey, LiveSessionSnapshot snapshot) {
        try {
            String json = mapper.writeValueAsString(snapshot);
            redis.opsForValue().set(chave(roomKey), json, TTL_SEGUNDOS, TimeUnit.SECONDS);
            redis.expire(chaveSeq(roomKey), TTL_SEGUNDOS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao gravar sessão ao vivo {} no Redis: {}", roomKey, e.getMessage());
        }
    }

    @Override
    public long proximoSeq(String roomKey) {
        try {
            Long seq = redis.opsForValue().increment(chaveSeq(roomKey));
            return seq == null ? System.currentTimeMillis() : seq;
        } catch (Exception e) {
            // Fallback monotônico: o cliente só compara ordem, e o relógio nunca anda para trás
            // dentro de uma mesma sessão. Melhor um seq grande demais do que uma mensagem perdida.
            log.warn("Falha ao incrementar seq de {} no Redis: {}", roomKey, e.getMessage());
            return System.currentTimeMillis();
        }
    }

    @Override
    public void remover(String roomKey) {
        try {
            redis.delete(chave(roomKey));
            redis.delete(chaveSeq(roomKey));
        } catch (Exception e) {
            log.warn("Falha ao remover sessão ao vivo {} do Redis: {}", roomKey, e.getMessage());
        }
    }

    @Override
    public Set<String> salasAtivas() {
        try {
            Set<String> chaves = redis.keys(PREFIXO + "*");
            if (chaves == null) return Collections.emptySet();
            return chaves.stream()
                    .filter(k -> !k.endsWith(SUFIXO_SEQ))
                    .map(k -> k.substring(PREFIXO.length()))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            log.warn("Falha ao listar sessões ao vivo no Redis: {}", e.getMessage());
            return Collections.emptySet();
        }
    }
}
