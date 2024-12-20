package com.baseapplication.core.model.superClasses;

import com.baseapplication.core.enums.StatusNotificacao;
import com.baseapplication.core.enums.TipoNotificacao;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.EstadoNotificacao;
import com.baseapplication.core.model.NotificacaoShow;
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
    @JoinColumn(name = "id_destinatario", nullable = true)
    private Usuario destinatario;

    @ManyToOne
    @JoinColumn(name = "id_banda_destino", nullable = true)
    private Banda bandaDestino;

    @Enumerated(EnumType.STRING)
    private StatusNotificacao statusNotificacao;

    private String mensagem;
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipoNotificacao;
    private String observacao;
}
