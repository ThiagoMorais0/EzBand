package com.baseapplication.core.dto.live;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Time;

/**
 * O mínimo que o servidor precisa saber sobre uma faixa do repertório.
 *
 * <p>Carregado uma única vez, na criação da sessão, e guardado no snapshot. Depois disso o
 * WebSocket nunca mais toca no banco: os comandos trafegam índices, e o resumo pós-show sai
 * inteiro daqui cruzado com {@link LiveFaixaTocada} — sem uma segunda consulta ao repertório
 * no encerramento.
 *
 * <p>Não traz {@code letra}: é TEXT, o cliente já carregou por REST, e não há razão de
 * arrastar isso para dentro do estado da sessão.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveFaixaInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer idx;
    private String titulo;
    private String artista;

    /** Duração cadastrada, em segundos. Null quando a banda não preencheu. */
    private Integer duracaoSegundos;

    public static LiveFaixaInfo de(Integer idx, String titulo, String artista, Time duracao) {
        Integer segundos = null;
        if (duracao != null) {
            segundos = (int) (duracao.toLocalTime().toSecondOfDay());
        }
        return new LiveFaixaInfo(idx, titulo, artista, segundos);
    }
}
