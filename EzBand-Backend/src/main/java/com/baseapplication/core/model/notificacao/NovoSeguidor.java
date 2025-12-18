package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("NOVO_SEGUIDOR")
@NoArgsConstructor
public class NovoSeguidor extends Notificacao {

    public NovoSeguidor(Usuario remetente, Long idDestinatario){
        super.setRemetenteId(remetente.getId());
        super.setDestinatarioId(idDestinatario);
        super.setRemetenteTipo(TipoParticipante.USUARIO);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);
        super.setPermiteResposta(false);
        super.setTitulo("Novo seguidor");
        super.setMensagem(remetente.getNome() + " começou a seguir você.");
    }

    @Override
    public String getTipoNotificacao() {
        return "NOVO_SEGUIDOR";
    }
}
