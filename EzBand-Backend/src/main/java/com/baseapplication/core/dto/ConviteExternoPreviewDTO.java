package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConviteExternoPreviewDTO {
    private boolean valido;
    private String nomeBanda;
    private String urlLogo;
    private String motivo;
}
