package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Entity
@DiscriminatorValue("USUARIO_EXPULSO_DE_BANDA")
@NoArgsConstructor
public class UsuarioExpulsoDeBanda extends Notificacao {

    public UsuarioExpulsoDeBanda(Long idBanda, Long idUsuario){
        super.setMensagem("Você foi expulso");

        super.setDestinatarioId(idBanda);
        super.setDestinatarioTipo(TipoParticipante.BANDA);

        super.setRemetenteId(idUsuario);
        super.setRemetenteTipo(TipoParticipante.USUARIO);
    }

    @Override
    public String getTipoNotificacao() {
        return "USUARIO_EXPULSO_DE_BANDA";
    }
}
