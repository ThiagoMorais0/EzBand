package com.baseapplication.core.live;

import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Store em memória, usado quando não há Redis configurado (dev local).
 *
 * <p>Perde tudo no restart e não funciona com mais de uma instância — as duas limitações
 * são aceitáveis em desenvolvimento e inaceitáveis em produção, que roda o
 * {@link RedisLiveSessionStore}.
 */
@Slf4j
public class InMemoryLiveSessionStore implements LiveSessionStore {

    private final Map<String, LiveSessionSnapshot> sessoes = new ConcurrentHashMap<>();
    private final Map<String, AtomicLong> sequencias = new ConcurrentHashMap<>();
    private final Map<String, Long> expiraEm = new ConcurrentHashMap<>();

    public InMemoryLiveSessionStore() {
        log.info("Modo Performance: usando store em memória (sem Redis configurado)");
    }

    @Override
    public LiveSessionSnapshot buscar(String roomKey) {
        Long expira = expiraEm.get(roomKey);
        if (expira != null && expira < System.currentTimeMillis()) {
            remover(roomKey);
            return null;
        }
        return sessoes.get(roomKey);
    }

    @Override
    public void salvar(String roomKey, LiveSessionSnapshot snapshot) {
        sessoes.put(roomKey, snapshot);
        expiraEm.put(roomKey, System.currentTimeMillis() + TTL_SEGUNDOS * 1000);
    }

    @Override
    public long proximoSeq(String roomKey) {
        return sequencias.computeIfAbsent(roomKey, k -> new AtomicLong()).incrementAndGet();
    }

    @Override
    public void remover(String roomKey) {
        sessoes.remove(roomKey);
        sequencias.remove(roomKey);
        expiraEm.remove(roomKey);
    }

    @Override
    public Set<String> salasAtivas() {
        return new HashSet<>(sessoes.keySet());
    }
}
