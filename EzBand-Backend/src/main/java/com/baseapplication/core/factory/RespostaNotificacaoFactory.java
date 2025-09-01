package com.baseapplication.core.factory;

import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.model.notificacao.SolicitacaoAgendarEnsaio;
import com.baseapplication.core.model.notificacao.SolicitacaoParaIngressarBanda;
import com.baseapplication.core.model.notificacao.resposta.RespostaAgendamentoEnsaioNotificacao;
import com.baseapplication.core.model.notificacao.resposta.RespostaSolicitacaoParaIngressarBandaNotificacao;
import com.baseapplication.core.model.superClasses.Notificacao;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RespostaNotificacaoFactory {

    public static Notificacao criarNotificacaoResposta(Notificacao notificacaoOriginal, String acao) {
        if (notificacaoOriginal instanceof SolicitacaoAgendarEnsaio solicitacao) {
            return new RespostaAgendamentoEnsaioNotificacao(solicitacao, Objects.requireNonNull(AcaoResposta.findByName(acao)));
        }
        if (notificacaoOriginal instanceof SolicitacaoParaIngressarBanda solicitacao){
            return new RespostaSolicitacaoParaIngressarBandaNotificacao(solicitacao, Objects.requireNonNull(AcaoResposta.findByName(acao)));
        }

        //TODO: outros tipos...
        throw new UnsupportedOperationException("Tipo de notificação não suportado para resposta");
    }
}
