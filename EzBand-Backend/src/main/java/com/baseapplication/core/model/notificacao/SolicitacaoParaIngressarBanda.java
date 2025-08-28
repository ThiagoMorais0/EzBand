package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SOLICITACAO_PARA_INGRESSAR_BANDA")
@NoArgsConstructor
public class SolicitacaoParaIngressarBanda extends Notificacao {

    public SolicitacaoParaIngressarBanda(Usuario usuario, Long idBanda, String instrumento) {
        super.setMensagem(usuario.getNome() + " deseja ingressar na banda tocando " + instrumento);

        super.setDestinatarioId(idBanda);
        super.setDestinatarioTipo(TipoParticipante.BANDA);

        super.setRemetenteId(usuario.getId());
        super.setRemetenteTipo(TipoParticipante.USUARIO);
    }

    @Override
    public String getTipoNotificacao() {
        return "SOLICITACAO_PARA_INGRESSAR_BANDA";
    }
}
