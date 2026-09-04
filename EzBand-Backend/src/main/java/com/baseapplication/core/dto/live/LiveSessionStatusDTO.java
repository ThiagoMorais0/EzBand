package com.baseapplication.core.dto.live;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Resposta de {@code GET /live/status}: o que a tela de repertório precisa saber antes de
 * entrar no palco.
 *
 * <p>Serve para o botão de Play mostrar "Entrar na sessão · 3 tocando" em vez de "Iniciar"
 * quando a banda já começou — evitando que o segundo músico ache que vai abrir algo novo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveSessionStatusDTO {

    private boolean ativa;
    private Long iniciadaEm;
    private Long iniciadaPor;
    private Integer idxAtual;
    private List<LiveMembroPresenca> conectados;

    public static LiveSessionStatusDTO inativa() {
        return new LiveSessionStatusDTO(false, null, null, null, List.of());
    }
}
