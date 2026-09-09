package com.baseapplication.core.enums;

/**
 * Tipos de mensagem do protocolo do Modo Performance (sessão ao vivo).
 *
 * <p>Mensagens que mutam o estado da sessão carregam sempre um {@code seq} monotônico
 * (INCR no store), e o cliente descarta qualquer mensagem cujo {@code seq} seja menor ou
 * igual ao último já visto. É isso que garante ordem entre múltiplos controladores e
 * depois de uma reconexão.
 */
public enum LiveMessageType {

    // ── Cliente → servidor ──────────────────────────────────────────────
    /** Pede o snapshot completo. Usado ao reconectar e ao voltar do background. */
    SYNC,
    /** Controlador seleciona uma faixa específica pelo índice. */
    SET_TRACK,
    /** Controlador avança. Não carrega índice de propósito: o servidor calcula a partir
     *  do estado dele, senão dois controladores simultâneos pulariam uma música. */
    NEXT_TRACK,
    /** Controlador retrocede. Mesma razão do {@link #NEXT_TRACK}. */
    PREV_TRACK,
    /** Controlador tira todo mundo da faixa e devolve a visão do setlist. */
    BACK_TO_SETLIST,
    /** Qualquer membro passa a controlar (aditivo, não remove ninguém). */
    CLAIM_CONTROL,
    /** Controlador abre mão do controle. */
    RELEASE_CONTROL,
    /** Controlador inicia o cronômetro regressivo do show. */
    TIMER_START,
    /** Controlador pausa o cronômetro. */
    TIMER_PAUSE,
    /** Controlador zera o cronômetro. */
    TIMER_RESET,
    /** Sinal silencioso entre músicos. Efêmero: não altera estado nem incrementa seq. */
    CUE,
    /** Controlador encerra a sessão e dispara a gravação do resumo pós-show. */
    END_SESSION,
    /**
     * Controlador acrescenta uma faixa que não estava no repertório do evento.
     *
     * <p>A faixa vai sempre para o <b>fim</b> da lista, nunca para o meio: {@code idxAtual},
     * o log {@link #TRACK_CHANGED} e o conjunto de tocadas de cada cliente são todos por
     * índice, e inserir no meio deslocaria tudo o que veio depois em toda a sala.
     *
     * <p>O conteúdo da faixa (título, artista, tom, BPM, letra) vem do cliente, que já tem o
     * repertório da banda carregado. É o que mantém a promessa de que o socket não volta ao
     * banco depois da abertura da sessão.
     */
    ADD_TRACK,

    // ── Servidor → cliente ──────────────────────────────────────────────
    /** Snapshot completo do estado + presença. Enviado ao conectar e em resposta a SYNC. */
    SESSION_STATE,
    TRACK_CHANGED,
    PRESENCE_CHANGED,
    CONTROLLER_CHANGED,
    TIMER_CHANGED,
    /** Faixa nova no fim do repertório da sessão, com o conteúdo para o cliente exibir. */
    TRACK_ADDED,
    SESSION_ENDED,
    ERROR
}
