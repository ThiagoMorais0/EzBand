package com.baseapplication.core.model.superClasses;

import com.baseapplication.core.enums.TipoParticipante;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "NOTIFICACAO")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // ou JOINED, dependendo
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING, length = 100)
public abstract class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String mensagem;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private boolean lida = false;
    private Long remetenteId;
    @Enumerated(EnumType.STRING)
    private TipoParticipante remetenteTipo;
    private Long destinatarioId;
    @Enumerated(EnumType.STRING)
    private TipoParticipante destinatarioTipo;
    @Column(length = 1000)
    private String urlImagem;
    private String titulo;

    public abstract String getTipoNotificacao();
}
