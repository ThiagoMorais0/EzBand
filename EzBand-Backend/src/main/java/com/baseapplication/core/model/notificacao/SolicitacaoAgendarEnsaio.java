package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("SOLICITACAO_PARA_AGENDAR_ENSAIO")
public class SolicitacaoAgendarEnsaio extends Notificacao {
}
