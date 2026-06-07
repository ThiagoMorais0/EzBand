package com.baseapplication.core.model.notificacao.resposta;

import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.notificacao.ConviteParaUsuarioIngressarBanda;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RESPOSTA_CONVITE_PARA_USUARIO_INGRESSAR_BANDA")
@NoArgsConstructor
public class RespostaConviteParaUsuarioIngressarBanda extends Notificacao {

    public RespostaConviteParaUsuarioIngressarBanda(ConviteParaUsuarioIngressarBanda convite, AcaoResposta acao, Usuario usuario) {
        super.setMensagem(acao.equals(AcaoResposta.ACEITAR) ?
                usuario.getNome() + " aceitou seu convite para ingressar na banda!" :
                usuario.getNome() + " recusou seu convite para ingressar na banda");
        super.setPermiteResposta(false);
        super.setRemetenteId(convite.getDestinatarioId());
        super.setRemetenteTipo(convite.getDestinatarioTipo());
        super.setTitulo(acao.equals(AcaoResposta.ACEITAR) ? "Convite aceito!" : "Convite recusado");
        super.setUrlImagem(convite.getUrlImagem());
        super.setDestinatarioId(convite.getRemetenteId());
        super.setDestinatarioTipo(convite.getRemetenteTipo());
    }

    @Override
    public String getTipoNotificacao() {
        return "RESPOSTA_CONVITE_PARA_USUARIO_INGRESSAR_BANDA";
    }
}
