package com.baseapplication.core.model.notificacao.resposta;

import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.notificacao.ConviteParaEvento;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RESPOSTA_CONVITE_PARA_EVENTO")
@NoArgsConstructor
public class RespostaConviteParaEvento extends Notificacao {

    public RespostaConviteParaEvento(ConviteParaEvento solicitacao, AcaoResposta acao, Usuario usuario) {
        super.setMensagem(acao.equals(AcaoResposta.ACEITAR) ?
                usuario.getNome() + " aceitou seu convite para o evento" :
                usuario.getNome() + " recusou seu convite para o evento");
        super.setPermiteResposta(false);
        // remetente é quem respondeu
        super.setRemetenteId(solicitacao.getDestinatarioId());
        super.setRemetenteTipo(solicitacao.getDestinatarioTipo());

        super.setTitulo(acao.equals(AcaoResposta.ACEITAR) ? "Convite aceito!" : "Convite recusado");
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
