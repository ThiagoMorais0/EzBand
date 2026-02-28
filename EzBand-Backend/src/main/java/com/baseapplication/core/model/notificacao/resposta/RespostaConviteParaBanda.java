package com.baseapplication.core.model.notificacao.resposta;

import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.notificacao.ConviteParaBanda;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RESPOSTA_CONVITE_PARA_BANDA")
@NoArgsConstructor
public class RespostaConviteParaBanda extends Notificacao {

    public RespostaConviteParaBanda(ConviteParaBanda convite, AcaoResposta acao, Usuario usuario) {
        super.setMensagem(acao.equals(AcaoResposta.ACEITAR) ?
                usuario.getNome() + " aceitou seu convite para entrar na banda!" :
                usuario.getNome() + " recusou seu convite para entrar na banda");
        super.setPermiteResposta(false);
        // remetente é quem respondeu (o usuário que foi convidado)
        super.setRemetenteId(convite.getDestinatarioId());
        super.setRemetenteTipo(convite.getDestinatarioTipo());

        super.setTitulo(acao.equals(AcaoResposta.ACEITAR) ? "Convite aceito!" : "Convite recusado");
        super.setUrlImagem(convite.getUrlImagem());

        // destinatário é quem enviou o convite (a banda)
        super.setDestinatarioId(convite.getRemetenteId());
        super.setDestinatarioTipo(convite.getRemetenteTipo());
    }

    @Override
    public String getTipoNotificacao() {
        return "RESPOSTA_CONVITE_PARA_BANDA";
    }
}
