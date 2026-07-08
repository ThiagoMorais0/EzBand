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
@DiscriminatorValue("SHOW_RECUSADO_PELO_LOCAL_EVENTO")
@NoArgsConstructor
public class ShowRecusadoPeloLocalEvento extends Notificacao {

    private Long idShow;

    public ShowRecusadoPeloLocalEvento(Show show, String motivo) {
        super.setLida(false);
        super.setPermiteResposta(false);
        super.setTitulo("Show recusado");
        super.setMensagem("O local \"" + show.getLocalEvento().getNome() +
                "\" recusou seu show agendado para " +
                show.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                (motivo != null && !motivo.isBlank() ? ". Motivo: " + motivo : "") + ".");
        super.setDestinatarioId(show.getBanda().getId());
        super.setDestinatarioTipo(TipoParticipante.BANDA);
        super.setRemetenteId(show.getLocalEvento().getId());
        super.setRemetenteTipo(TipoParticipante.LOCAL_EVENTO);
        super.setUrlImagem(show.getLocalEvento().getUrlFotoPerfil());
        this.idShow = show.getId();
    }

    @Override
    public String getTipoNotificacao() {
        return "SHOW_RECUSADO_PELO_LOCAL_EVENTO";
    }
}
