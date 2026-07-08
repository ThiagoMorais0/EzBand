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
@DiscriminatorValue("SOLICITACAO_PARA_AGENDAR_SHOW")
@NoArgsConstructor
public class SolicitacaoAgendarShow extends Notificacao {

    private Long idShow;

    public SolicitacaoAgendarShow(Show show, Long destinatarioId) {
        super.setLida(false);
        super.setTitulo("Novo agendamento!");
        super.setMensagem("Banda \"" + show.getBanda().getNome() +
                "\" está solicitando um agendamento em " +
                show.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " às " + show.getHorarioInicio());
        super.setDestinatarioId(destinatarioId);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);
        super.setRemetenteId(show.getBanda().getId());
        super.setRemetenteTipo(TipoParticipante.BANDA);
        this.idShow = show.getId();
    }

    @Override
    public String getTipoNotificacao() {
        return "SOLICITACAO_PARA_AGENDAR_SHOW";
    }
}
