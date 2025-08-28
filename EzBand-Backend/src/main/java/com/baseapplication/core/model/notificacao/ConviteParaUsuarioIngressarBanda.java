package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("CONVITE_PARA_USUARIO_INGRESSAR_BANDA")
@NoArgsConstructor
public class ConviteParaUsuarioIngressarBanda extends Notificacao {

    public ConviteParaUsuarioIngressarBanda(Long idBanda, Long idUsuario){
        super.setMensagem("Você está sendo convidado para ingressar a banda!");

        super.setDestinatarioId(idBanda);
        super.setDestinatarioTipo(TipoParticipante.BANDA);

        super.setRemetenteId(idUsuario);
        super.setRemetenteTipo(TipoParticipante.USUARIO);
    }

    @Override
    public String getTipoNotificacao() {
        return "CONVITE_PARA_USUARIO_INGRESSAR_BANDA";
    }
}
