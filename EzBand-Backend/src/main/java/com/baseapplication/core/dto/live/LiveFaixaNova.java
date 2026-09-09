package com.baseapplication.core.dto.live;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Faixa acrescentada ao vivo, no meio do show.
 *
 * <p>Trafega em duas direções: sobe no {@code ADD_TRACK} de quem controla e desce no
 * {@code TRACK_ADDED} para toda a sala. É de propósito que o conteúdo venha do cliente —
 * quem adicionou já tem o repertório da banda carregado, e assim o WebSocket continua sem
 * voltar ao banco depois de abrir a sessão.
 *
 * <p>Não é o que fica guardado: o snapshot só retém o {@link LiveFaixaInfo} enxuto
 * (título, artista, duração), que é o que o resumo pós-show precisa. Letra e cifra são
 * pesadas e existem apenas nesta mensagem, para o cliente montar a tela sem um REST a mais
 * no pior momento possível.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveFaixaNova {

    /**
     * Limite defensivo. Uma letra de música não chega perto disso; o teto existe só para que
     * um cliente adulterado não empurre um texto gigante para todo mundo na sala.
     */
    public static final int LIMITE_LETRA = 20_000;
    public static final int LIMITE_TEXTO_CURTO = 255;

    private String titulo;
    private String artista;

    /** Duração cadastrada, em segundos. Null quando ninguém preencheu. */
    private Integer duracaoSegundos;

    private Integer tonalidade;
    private Integer bpm;
    private String letra;

    /** Índice que a faixa recebeu na sessão. Preenchido pelo servidor no eco, ignorado na subida. */
    private Integer idx;

    /** Corta o que passar dos limites em vez de recusar: no palco, faixa truncada é melhor que faixa nenhuma. */
    public void normalizar() {
        this.titulo = truncar(this.titulo, LIMITE_TEXTO_CURTO);
        this.artista = truncar(this.artista, LIMITE_TEXTO_CURTO);
        this.letra = truncar(this.letra, LIMITE_LETRA);
        if (this.titulo != null) this.titulo = this.titulo.trim();
        if (this.artista != null) this.artista = this.artista.trim();
        if (this.duracaoSegundos != null && this.duracaoSegundos < 0) this.duracaoSegundos = null;
    }

    public boolean temTitulo() {
        return titulo != null && !titulo.isBlank();
    }

    private static String truncar(String valor, int limite) {
        if (valor == null) return null;
        return valor.length() <= limite ? valor : valor.substring(0, limite);
    }
}
