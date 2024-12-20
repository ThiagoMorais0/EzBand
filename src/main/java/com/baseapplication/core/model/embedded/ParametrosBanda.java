package com.baseapplication.core.model.embedded;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class ParametrosBanda {
    private Boolean permiteEntradaPorConvite = true;
    private Boolean exigirAprovacaoCompromissos = true;
    private Boolean listarObservacaoRepertorio = true;
}
