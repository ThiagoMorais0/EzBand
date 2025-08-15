package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("APROVACAO_EVENTO")
public class AprovacaoEvento extends Notificacao {
    private TipoEvento tipoEvento;
}
