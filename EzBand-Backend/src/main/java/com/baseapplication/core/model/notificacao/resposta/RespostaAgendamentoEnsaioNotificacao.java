package com.baseapplication.core.model.notificacao.resposta;

import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.enums.TipoAvaliacao;
import com.baseapplication.core.model.notificacao.RespostaNotificacao;
import com.baseapplication.core.model.notificacao.SolicitacaoAgendarEnsaio;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RESPOSTA_AGENDAMENTO_ENSAIO")
@NoArgsConstructor
public class RespostaAgendamentoEnsaioNotificacao extends Notificacao {

    public RespostaAgendamentoEnsaioNotificacao(SolicitacaoAgendarEnsaio solicitacao, AcaoResposta acao) {
        super.setMensagem(acao.equals(AcaoResposta.ACEITAR) ? "" +
                "O agendamento foi confirmado!" :
                "O agendamento foi recusado.");

        // remetente é quem respondeu
        super.setRemetenteId(solicitacao.getDestinatarioId());
        super.setRemetenteTipo(solicitacao.getDestinatarioTipo());

        // destinatário é quem solicitou
        super.setDestinatarioId(solicitacao.getRemetenteId());
        super.setDestinatarioTipo(solicitacao.getRemetenteTipo());
    }

    @Override
    public String getTipoNotificacao() {
        return "RESPOSTA_AGENDAMENTO_ENSAIO";
    }
}