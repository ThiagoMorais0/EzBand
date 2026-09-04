package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@DiscriminatorValue("APROVACAO_EVENTO")
public class AprovacaoEvento extends Notificacao {
    // Herança SINGLE_TABLE: este campo divide a coluna `tipo_evento` com
    // NovoEventoMarcado, que a mapeia como STRING. Sem o @Enumerated aqui o
    // padrão seria ORDINAL, e o Hibernate tentaria converter a coluna para
    // smallint em todo boot — algo que o Postgres recusa por já haver 'SHOW'
    // gravado nela.
    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    @Override
    public String getTipoNotificacao() {
        return "APROVACAO_EVENTO";
    }
}
