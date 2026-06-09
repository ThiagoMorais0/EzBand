package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@Setter
@Entity
@DiscriminatorValue("SOLICITACAO_PARA_AGENDAR_ENSAIO")
@NoArgsConstructor
public class SolicitacaoAgendarEnsaio extends Notificacao {

    private Long idEnsaio;

    public SolicitacaoAgendarEnsaio(Ensaio ensaio, Long destinatarioUserId) {
        super.setLida(false);
        super.setMensagem("Banda \"" + ensaio.getBanda().getNome() +
                "\" está solicitando um agendamento em " +
                ensaio.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " às " + ensaio.getHorarioInicio());

        super.setDestinatarioId(destinatarioUserId);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);

        super.setRemetenteId(ensaio.getBanda().getId());
        super.setRemetenteTipo(TipoParticipante.BANDA);
    }

    @Override
    public String getTipoNotificacao() {
        return "SOLICITACAO_PARA_AGENDAR_ENSAIO";
    }
}
