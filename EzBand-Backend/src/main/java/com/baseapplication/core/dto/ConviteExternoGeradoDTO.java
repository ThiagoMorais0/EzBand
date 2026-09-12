package com.baseapplication.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConviteExternoGeradoDTO {
    private String token;
    private String link;
    /** Código curto já formatado para leitura humana (ex.: "4K2P-WX7N"). */
    private String codigo;
}
