package com.baseapplication.core.live;

import com.baseapplication.core.dto.live.LiveSessionSnapshot;

import java.util.Set;

/**
 * Persistência efêmera do estado das sessões ao vivo.
 *
 * <p>Duas implementações: {@link RedisLiveSessionStore} quando {@code spring.data.redis.host}
 * está configurado (produção), {@link InMemoryLiveSessionStore} no resto (dev local sem
 * container de Redis). A escolha é feita em {@code LiveSessionStoreConfig}.
 *
 * <p>Nenhum método aqui toca JPA. O caminho quente do WebSocket não pode encostar no pool
 * do Hikari — foi exatamente essa combinação (conexão longa segurando conexão de banco)
 * que derrubou o SSE de notificações.
 */
public interface LiveSessionStore {

    /** TTL de uma sessão sem atividade. Um show mais a passagem de som cabem folgados. */
    long TTL_SEGUNDOS = 8 * 60 * 60L;

    LiveSessionSnapshot buscar(String roomKey);

    /** Grava e renova o TTL. */
    void salvar(String roomKey, LiveSessionSnapshot snapshot);

    /** Incremento atômico do contador monotônico da sala. */
    long proximoSeq(String roomKey);

    void remover(String roomKey);

    /** Salas com estado vivo — usado pelo varredor de sessões abandonadas. */
    Set<String> salasAtivas();
}
