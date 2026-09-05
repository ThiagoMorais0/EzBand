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
@DiscriminatorValue("SESSAO_PALCO_INICIADA")
@NoArgsConstructor
@Getter
public class SessaoPalcoIniciada extends Notificacao {

    private Long idEvento;

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    public SessaoPalcoIniciada(
            Long idEvento,
            TipoEvento tipoEvento,
            Long idBanda,
            String nomeBanda,
            String urlLogoBanda,
            String local,
            Long idIniciador,
            String nomeIniciador,
            Long idDestinatario) {

        this.idEvento = idEvento;
        this.tipoEvento = tipoEvento;

        String tipoNome = tipoEvento == TipoEvento.SHOW ? "show" : "ensaio";
        String url = "/banda/" + idBanda + "/" + tipoNome + "/" + idEvento + "/palco";

        super.setTitulo("Sessão ao vivo iniciada");
        super.setMensagem(local != null && !local.isBlank()
                ? nomeIniciador + " iniciou a sessão do palco para o " + tipoNome + " em " + local
                : nomeIniciador + " iniciou a sessão do palco para o " + tipoNome + " da " + nomeBanda);
        super.setUrl(url);
        super.setUrlImagem(urlLogoBanda);
        super.setPermiteResposta(false);

        super.setRemetenteId(idIniciador);
        super.setRemetenteTipo(TipoParticipante.USUARIO);
        super.setDestinatarioId(idDestinatario);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);
    }

    @Override
    public String getTipoNotificacao() {
        return "SESSAO_PALCO_INICIADA";
    }
}
