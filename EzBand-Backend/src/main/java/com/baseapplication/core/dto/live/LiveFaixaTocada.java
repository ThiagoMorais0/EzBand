package com.baseapplication.core.dto.live;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Uma passagem por uma faixa durante a sessão ao vivo.
 *
 * <p>Acumula no snapshot durante o show inteiro (é minúsculo) e desce para o Postgres
 * uma única vez, no encerramento — nunca a cada troca de música.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveFaixaTocada implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idx;
    private String titulo;

    /** Instante (epoch ms) em que a faixa entrou na tela da banda. */
    private Long de;

    /** Instante (epoch ms) em que a banda saiu dela. Null enquanto é a faixa atual. */
    private Long ate;

    public LiveFaixaTocada(Integer idx, String titulo, Long de) {
        this.idx = idx;
        this.titulo = titulo;
        this.de = de;
    }
}
