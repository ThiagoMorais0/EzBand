package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Retrato publico de um convite: alimenta a tela que o convidado ve antes de entrar na banda,
 * tanto deslogado (link aberto pela primeira vez) quanto ja logado.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConviteExternoPreviewDTO {

    private boolean valido;
    private String motivo;
    private Long idBanda;
    private String nomeBanda;
    private String urlLogo;
    private String nomeRemetente;
    private String instrumento;
    private int quantidadeEventos;
    /** So tem significado quando a requisicao chega autenticada. */
    private boolean jaEMembro;

    public static ConviteExternoPreviewDTO invalido(String motivo) {
        ConviteExternoPreviewDTO dto = new ConviteExternoPreviewDTO();
        dto.valido = false;
        dto.motivo = motivo;
        return dto;
    }
}
