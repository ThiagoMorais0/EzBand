package com.baseapplication.core.service;

import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import com.baseapplication.core.live.LiveUsuarioSessao;

import java.util.Set;

/**
 * Regras da sessão ao vivo (Modo Performance).
 *
 * <p>Toda mutação é serializada por sala e devolve o snapshot já persistido e com o
 * {@code seq} incrementado, pronto para o handler transmitir. Quando um comando não muda
 * nada (dois controladores pedindo a mesma faixa, por exemplo) o retorno é {@code null} e
 * nada é transmitido.
 */
public interface LiveSessionService {

    /** Entra na sessão, criando-a se este for o primeiro a chegar. */
    LiveSessionSnapshot abrirOuEntrar(LiveUsuarioSessao usuario);

    LiveSessionSnapshot buscar(String roomKey);

    LiveSessionSnapshot definirFaixa(LiveUsuarioSessao usuario, Integer idx);

    /** Avança ({@code delta = 1}) ou volta ({@code delta = -1}) a partir do estado do servidor. */
    LiveSessionSnapshot moverFaixa(LiveUsuarioSessao usuario, int delta);

    LiveSessionSnapshot voltarAoSetlist(LiveUsuarioSessao usuario);

    LiveSessionSnapshot assumirControle(LiveUsuarioSessao usuario);

    LiveSessionSnapshot liberarControle(LiveUsuarioSessao usuario);

    /**
     * Recalcula o estado de "controle órfão" a partir de quem está de fato conectado.
     *
     * <p>Chamado a cada entrada e saída. Devolve o snapshot só quando o estado mudou —
     * o controlador que reconecta em três segundos não gera evento nenhum.
     */
    LiveSessionSnapshot reavaliarControle(String roomKey, Set<Long> idsConectados);

    /**
     * Inicia ou retoma o cronômetro regressivo do show.
     *
     * <p>O servidor guarda o instante de fim previsto, não o tempo restante: quem entra no
     * minuto 40 já vê o número certo, e não há drift entre os relógios da banda.
     */
    LiveSessionSnapshot iniciarTimer(LiveUsuarioSessao usuario, Integer duracaoSegundos);

    LiveSessionSnapshot pausarTimer(LiveUsuarioSessao usuario);

    LiveSessionSnapshot zerarTimer(LiveUsuarioSessao usuario);

    /** Encerra a sessão, fecha a faixa em aberto e devolve o snapshot final para persistência. */
    LiveSessionSnapshot encerrar(LiveUsuarioSessao usuario);

    /** Fecha uma sala abandonada sem passar por permissão — uso exclusivo do varredor. */
    LiveSessionSnapshot encerrarPorAbandono(String roomKey);

    void remover(String roomKey);
}
