package com.baseapplication.core.model.superClasses;

import com.baseapplication.core.enums.TipoNotificacao;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "NOTIFICACAO")
@Getter
@Setter
public abstract class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_remetente")
    private Usuario remetente;

    @ManyToOne
    @JoinColumn(name = "id_destinatario")
    private Usuario destinatario;
    private String mensagem;
    private LocalDate data;
    private TipoNotificacao tipoNotificacao;
}
