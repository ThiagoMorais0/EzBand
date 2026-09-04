package com.baseapplication.core.live;

import lombok.Getter;

/**
 * Comando recusado dentro da sessão ao vivo (sem permissão, índice fora do repertório,
 * sessão já encerrada).
 *
 * <p>Vira uma mensagem {@code ERROR} para quem mandou e não derruba o socket: no meio de um
 * show, fechar a conexão de alguém por causa de um toque inválido é o pior desfecho possível.
 */
@Getter
public class LiveComandoInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String codigo;

    public LiveComandoInvalidoException(String codigo, String mensagem) {
        super(mensagem);
        this.codigo = codigo;
    }

    public static LiveComandoInvalidoException semControle() {
        return new LiveComandoInvalidoException("SEM_CONTROLE",
                "Só quem está controlando a sessão pode mudar a música.");
    }

    public static LiveComandoInvalidoException semSessao() {
        return new LiveComandoInvalidoException("SEM_SESSAO",
                "Esta sessão ao vivo não está mais ativa.");
    }

    public static LiveComandoInvalidoException faixaInvalida() {
        return new LiveComandoInvalidoException("FAIXA_INVALIDA",
                "Essa faixa não existe no repertório do evento.");
    }
}
