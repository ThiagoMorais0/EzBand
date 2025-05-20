package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EdicaoBandaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String categoria;
    private Boolean permiteEntradaPorConvite;
    private Boolean exigirAprovacaoCompromissos;
}
