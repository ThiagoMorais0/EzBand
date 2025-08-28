package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CONVITE_PARA_BANDA")
public class ConviteParaBanda extends Notificacao {

    @Override
    public String getTipoNotificacao() {
        return "CONVITE_PARA_BANDA";
    }
}
