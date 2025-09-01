package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("SOLICITACAO_PARA_INGRESSAR_BANDA")
@NoArgsConstructor
@Setter
@Getter
public class SolicitacaoParaIngressarBanda extends Notificacao {

    private String instrumento;

    public SolicitacaoParaIngressarBanda(Usuario usuario, Banda banda, String instrumento) {
        super.setMensagem(usuario.getNome() + " deseja ingressar na banda tocando " + instrumento.toLowerCase());

        super.setTitulo(banda.getNome() + " - Solicitação");
        super.setUrlImagem(banda.getUrlLogo());
        this.setInstrumento(instrumento);

        super.setDestinatarioId(banda.getId());
        super.setDestinatarioTipo(TipoParticipante.BANDA);

        super.setRemetenteId(usuario.getId());
        super.setRemetenteTipo(TipoParticipante.USUARIO);
    }

    @Override
    public String getTipoNotificacao() {
        return "SOLICITACAO_PARA_INGRESSAR_BANDA";
    }
}
