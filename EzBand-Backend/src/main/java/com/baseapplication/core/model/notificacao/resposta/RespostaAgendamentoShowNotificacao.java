package com.baseapplication.core.model.notificacao.resposta;

import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.model.notificacao.SolicitacaoAgendarShow;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RESPOSTA_AGENDAMENTO_SHOW")
@NoArgsConstructor
public class RespostaAgendamentoShowNotificacao extends Notificacao {

    public RespostaAgendamentoShowNotificacao(SolicitacaoAgendarShow solicitacao, AcaoResposta acao) {
        super.setMensagem(acao.equals(AcaoResposta.ACEITAR) ? 
                "O agendamento foi confirmado!" :
                "O agendamento foi recusado.");
        super.setPermiteResposta(false);

        // remetente é quem respondeu
        super.setRemetenteId(solicitacao.getDestinatarioId());
        super.setRemetenteTipo(solicitacao.getDestinatarioTipo());

        super.setUrlImagem(solicitacao.getUrlImagem());
        super.setTitulo(acao.equals(AcaoResposta.ACEITAR) ? 
                "Agendamento foi confirmado!" :
                "Agendamento foi recusado.");

        // destinatário é quem solicitou
        super.setDestinatarioId(solicitacao.getRemetenteId());
        super.setDestinatarioTipo(solicitacao.getRemetenteTipo());
    }

    @Override
    public String getTipoNotificacao() {
        return "RESPOSTA_AGENDAMENTO_SHOW";
    }
}
