package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.Estudio;
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
@DiscriminatorValue("ENSAIO_CANCELADO_PELO_ESTUDIO")
@NoArgsConstructor
public class EnsaioCanceladoPeloEstudio extends Notificacao {

    private Long idEnsaio;

    public EnsaioCanceladoPeloEstudio(Ensaio ensaio, Estudio estudio, String motivo) {
        super.setLida(false);
        super.setPermiteResposta(false);
        super.setTitulo("Ensaio cancelado pelo estúdio");
        String msg = "O estúdio \"" + estudio.getNome() + "\" cancelou seu ensaio agendado para " +
                ensaio.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".";
        if (motivo != null && !motivo.isBlank()) {
            msg += " Motivo: " + motivo;
        }
        super.setMensagem(msg);
        super.setDestinatarioId(ensaio.getBanda().getId());
        super.setDestinatarioTipo(TipoParticipante.BANDA);
        super.setRemetenteId(estudio.getId());
        super.setRemetenteTipo(TipoParticipante.ESTUDIO);
        super.setUrlImagem(estudio.getUrlFotoPerfil());
        this.idEnsaio = ensaio.getId();
    }

    @Override
    public String getTipoNotificacao() {
        return "ENSAIO_CANCELADO_PELO_ESTUDIO";
    }
}
