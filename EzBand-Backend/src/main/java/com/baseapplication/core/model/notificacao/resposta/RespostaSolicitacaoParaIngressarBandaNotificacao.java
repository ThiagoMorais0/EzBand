package com.baseapplication.core.model.notificacao.resposta;

import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.model.notificacao.SolicitacaoParaIngressarBanda;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RESPOSTA_INGRESSAR_BANDA")
@NoArgsConstructor
public class RespostaSolicitacaoParaIngressarBandaNotificacao extends Notificacao {

    public RespostaSolicitacaoParaIngressarBandaNotificacao(SolicitacaoParaIngressarBanda solicitacao, AcaoResposta acao) {
        super.setMensagem(acao.equals(AcaoResposta.ACEITAR) ?
                "Você ingressou a banda!" :
                "Sua solicitação para ingressar a banda foi recusada. Resposta:" + solicitacao.getMensagem());
        super.setPermiteResposta(false);
        // remetente é quem respondeu
        super.setRemetenteId(solicitacao.getDestinatarioId());
        super.setRemetenteTipo(solicitacao.getDestinatarioTipo());

        super.setTitulo(acao.equals(AcaoResposta.ACEITAR) ? "Solicitação aceita!" : "Solicitação recusada");
        super.setUrlImagem(solicitacao.getUrlImagem());

        // destinatário é quem solicitou
        super.setDestinatarioId(solicitacao.getRemetenteId());
        super.setDestinatarioTipo(solicitacao.getRemetenteTipo());
    }

    @Override
    public String getTipoNotificacao() {
        return "RESPOSTA_INGRESSAR_BANDA";
    }
}
