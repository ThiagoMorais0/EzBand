package com.baseapplication.core.model.superClasses;

import com.baseapplication.core.enums.TipoParticipante;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "NOTIFICACAO")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // ou JOINED, dependendo
@DiscriminatorColumn(name = "tipo")
public abstract class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String mensagem;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCriacao = new Date();

    private boolean lida;

    private Long remetenteId;
    @Enumerated(EnumType.STRING)
    private TipoParticipante remetenteTipo;

    // Quem vai receber
    private Long destinatarioId;
    @Enumerated(EnumType.STRING)
    private TipoParticipante destinatarioTipo;
}
