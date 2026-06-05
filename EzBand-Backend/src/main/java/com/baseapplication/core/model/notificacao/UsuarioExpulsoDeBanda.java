package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("USUARIO_EXPULSO_DE_BANDA")
@NoArgsConstructor
public class UsuarioExpulsoDeBanda extends Notificacao {

    public UsuarioExpulsoDeBanda(Banda banda, Long idUsuario){
        super.setTitulo("Você foi expulso");
        super.setMensagem("Você foi removido da banda " + banda.getNome() + ".");
        super.setUrlImagem(banda.getUrlLogo());

        super.setDestinatarioId(idUsuario);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);

        super.setRemetenteId(banda.getId());
        super.setRemetenteTipo(TipoParticipante.BANDA);
        super.setPermiteResposta(false);
    }

    @Override
    public String getTipoNotificacao() {
        return "USUARIO_EXPULSO_DE_BANDA";
    }
}
