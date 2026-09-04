package com.baseapplication.core.dto.live;

import com.baseapplication.core.enums.LiveMessageType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mensagem recebida de um cliente da sessão ao vivo.
 *
 * <p>Campos desconhecidos são ignorados de propósito: um cliente com uma versão mais nova
 * do app não pode derrubar a sessão de quem ainda não recarregou a página no meio do show.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LiveClientMessage {

    private LiveMessageType tipo;

    /** SET_TRACK. */
    private Integer idx;

    /** TIMER_START. */
    private Integer duracaoSegundos;

    /** CUE. */
    private String cueTipo;
    private String cueTexto;
}
