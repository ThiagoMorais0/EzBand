package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Resultado do aceite de um convite por link. */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConviteAceitoDTO {
    private Long idBanda;
    private String nomeBanda;
    /** true quando o usuario ja era membro da banda antes de abrir o link. */
    private boolean jaEraMembro;
}
