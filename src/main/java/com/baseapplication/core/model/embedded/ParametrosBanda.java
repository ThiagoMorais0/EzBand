package com.baseapplication.core.model.embedded;

import jakarta.persistence.Embeddable;

@Embeddable
public class ParametrosBanda {
    private Boolean permiteEntradaPorConvite;
    private Boolean exigirAprovacaoCompromissos;
    private Boolean listarObservacaoRepertorio;
}
