package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Entity
@DiscriminatorValue("CONVITE_PARA_EVENTO")
@NoArgsConstructor
@Getter
public class ConviteParaEvento extends Notificacao {

    private Long idEventoConvite;
    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEventoConvite;
    private Long idUsuarioConvidado;
    private BigDecimal cacheConvidado;
    private String instrumentosConvidado;

    public ConviteParaEvento(Evento evento, Long idUsuario, BigDecimal cache, String instrumentos) {
        StringBuilder mensagem = new StringBuilder("A banda " + evento.getBanda().getNome() + " te convidou para um " +
                evento.getTipoEvento().getDescricao() + " em " +
                evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " às " +
                evento.getHorarioInicio());

        if(cache != null){
            mensagem.append(" com cache de R$ ").append(cache);
        }

        this.idEventoConvite = evento.getId();
        this.idUsuarioConvidado = idUsuario;
        this.tipoEventoConvite = evento.getTipoEvento();
        this.cacheConvidado = cache;
        this.instrumentosConvidado = instrumentos;

        super.setMensagem(mensagem.toString());

        super.setTitulo("Convite para evento");
        super.setUrlImagem(evento.getBanda().getUrlLogo());

        super.setRemetenteId(evento.getBanda().getId());
        super.setRemetenteTipo(TipoParticipante.BANDA);

        super.setDestinatarioId(idUsuario);
        super.setDestinatarioTipo(TipoParticipante.USUARIO);
    }

    @Override
    public String getTipoNotificacao() {
        return "CONVITE_PARA_EVENTO";
    }
}
