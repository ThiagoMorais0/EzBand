package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("CONVITE_PARA_USUARIO_INGRESSAR_BANDA")
@NoArgsConstructor
@Getter
@Setter
public class ConviteParaUsuarioIngressarBanda extends Notificacao {

    private String instrumento;

    public ConviteParaUsuarioIngressarBanda(Long idUsuario, Banda banda){
        super.setTitulo("Convite");
        super.setMensagem("Você está sendo convidado para ingressar a banda " + banda.getNome() + "!");
        super.setUrlImagem(banda.getUrlLogo());

        super.setDestinatarioId(idUsuario);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);

        super.setRemetenteId(banda.getId());
        super.setRemetenteTipo(TipoParticipante.BANDA);
        
        this.instrumento = ""; // Será definido pelo usuário ao aceitar
    }

    @Override
    public String getTipoNotificacao() {
        return "CONVITE_PARA_USUARIO_INGRESSAR_BANDA";
    }
}
