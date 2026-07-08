package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@Entity
@DiscriminatorValue("SHOW_APROVADO_PELO_LOCAL_EVENTO")
@NoArgsConstructor
public class ShowAprovadoPeloLocalEvento extends Notificacao {

    private Long idShow;

    public ShowAprovadoPeloLocalEvento(Show show) {
        super.setLida(false);
        super.setPermiteResposta(false);
        super.setTitulo("Show aprovado!");
        super.setMensagem("O local \"" + show.getLocalEvento().getNome() +
                "\" aprovou seu show agendado para " +
                show.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
        super.setDestinatarioId(show.getBanda().getId());
        super.setDestinatarioTipo(TipoParticipante.BANDA);
        super.setRemetenteId(show.getLocalEvento().getId());
        super.setRemetenteTipo(TipoParticipante.LOCAL_EVENTO);
        super.setUrlImagem(show.getLocalEvento().getUrlFotoPerfil());
        this.idShow = show.getId();
    }

    @Override
    public String getTipoNotificacao() {
        return "SHOW_APROVADO_PELO_LOCAL_EVENTO";
    }
}
