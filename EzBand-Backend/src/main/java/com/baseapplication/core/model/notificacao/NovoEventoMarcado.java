package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("NOVO_EVENTO_MARCADO")
@NoArgsConstructor
@Getter
public class NovoEventoMarcado extends Notificacao {

    private Long idEvento;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    public NovoEventoMarcado(
            Long idEvento,
            TipoEvento tipoEvento,
            Long idBanda,
            String nomeBanda,
            String urlLogoBanda,
            String local,
            Long idCriador,
            String nomeCriador,
            Long idDestinatario) {

        this.idEvento = idEvento;
        this.tipoEvento = tipoEvento;

        String tipoNome = tipoEvento == TipoEvento.SHOW ? "show" : "ensaio";
        String url = tipoEvento == TipoEvento.SHOW
                ? "/banda/" + idBanda + "/show/" + idEvento
                : "/banda/" + idBanda + "/ensaio/" + idEvento;

        super.setTitulo("Novo " + tipoNome + " marcado!");
        super.setMensagem(nomeCriador + " está marcando um " + tipoNome + " em " + local);
        super.setUrl(url);
        super.setUrlImagem(urlLogoBanda);
        super.setPermiteResposta(false);

        super.setRemetenteId(idCriador);
        super.setRemetenteTipo(TipoParticipante.USUARIO);
        super.setDestinatarioId(idDestinatario);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);
    }

    @Override
    public String getTipoNotificacao() {
        return "NOVO_EVENTO_MARCADO";
    }
}
