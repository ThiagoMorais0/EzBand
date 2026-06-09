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
@DiscriminatorValue("ENSAIO_APROVADO_PELO_ESTUDIO")
@NoArgsConstructor
public class EnsaioAprovadoPeloEstudio extends Notificacao {

    private Long idEnsaio;

    public EnsaioAprovadoPeloEstudio(Ensaio ensaio, Estudio estudio) {
        super.setLida(false);
        super.setPermiteResposta(false);
        super.setTitulo("Ensaio aprovado!");
        super.setMensagem("O estúdio \"" + estudio.getNome() + "\" aprovou seu ensaio agendado para " +
                ensaio.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
        super.setDestinatarioId(ensaio.getBanda().getId());
        super.setDestinatarioTipo(TipoParticipante.BANDA);
        super.setRemetenteId(estudio.getId());
        super.setRemetenteTipo(TipoParticipante.ESTUDIO);
        super.setUrlImagem(estudio.getUrlFotoPerfil());
        this.idEnsaio = ensaio.getId();
    }

    @Override
    public String getTipoNotificacao() {
        return "ENSAIO_APROVADO_PELO_ESTUDIO";
    }
}
