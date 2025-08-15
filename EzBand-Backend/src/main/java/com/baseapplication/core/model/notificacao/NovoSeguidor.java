package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("NOVO_SEGUIDOR")
public class NovoSeguidor extends Notificacao {
}
