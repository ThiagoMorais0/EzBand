package com.baseapplication.core.dto.live;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Estado do cronômetro regressivo do show.
 *
 * <p>O servidor guarda o <b>instante de fim previsto</b> em epoch ms, não o tempo restante.
 * Cada cliente calcula {@code restante = fimPrevistoEpochMs - Date.now()}, então quem
 * reconecta no minuto 40 já entra com o número certo e não há drift acumulado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveTimerState implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean ativo;

    /** Instante (epoch ms) em que o cronômetro chega a zero. Null enquanto nunca iniciado. */
    private Long fimPrevistoEpochMs;

    /** Quando pausado, quantos ms sobravam. Null enquanto rodando. */
    private Long pausadoRestanteMs;

    /** Duração cheia configurada, para o botão de reset. */
    private Integer duracaoSegundos;
}
