package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.Date;

@Entity
@DiscriminatorValue("SOLICITACAO_PARA_AGENDAR_ENSAIO")
@NoArgsConstructor
public class SolicitacaoAgendarEnsaio extends Notificacao {

    public SolicitacaoAgendarEnsaio(Ensaio ensaio){
        super.setLida(false);
        super.setMensagem("Banda \"" + ensaio.getBanda().getNome() +
                "\" está solicitando um agendamento em " +
                ensaio.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " às " + ensaio.getHorarioInicio());

        super.setDestinatarioId(ensaio.getEstudio().getId());
        super.setDestinatarioTipo(TipoParticipante.ESTUDIO);

        super.setRemetenteId(ensaio.getBanda().getId());
        super.setRemetenteTipo(TipoParticipante.BANDA);
    }
}
