package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@DiscriminatorValue("CONVITE_PARA_USUARIO_INGRESSAR_BANDA")
@NoArgsConstructor
@Getter
@Setter
public class ConviteParaUsuarioIngressarBanda extends Notificacao {

    private String instrumento;

    @Column(unique = true)
    private String linkToken;

    /** Eventos em que o convidado entra ao aceitar, no formato "SHOW:12,ENSAIO:33". */
    @Column(length = 1000)
    private String eventos;

    public ConviteParaUsuarioIngressarBanda(Long idUsuario, Banda banda){
        this(idUsuario, banda, null);
    }

    public ConviteParaUsuarioIngressarBanda(Long idUsuario, Banda banda, String eventos){
        super.setTitulo("Convite");
        super.setMensagem("Você está sendo convidado para ingressar a banda " + banda.getNome() + "!");
        super.setUrlImagem(banda.getUrlLogo());
        this.linkToken = UUID.randomUUID().toString();

        super.setDestinatarioId(idUsuario);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);

        super.setRemetenteId(banda.getId());
        super.setRemetenteTipo(TipoParticipante.BANDA);
        
        this.instrumento = ""; // Será definido pelo usuário ao aceitar
        this.eventos = eventos;
    }

    @Override
    public String getTipoNotificacao() {
        return "CONVITE_PARA_USUARIO_INGRESSAR_BANDA";
    }
}
