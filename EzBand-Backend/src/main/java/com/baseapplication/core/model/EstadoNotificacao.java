package com.baseapplication.core.model;

import com.baseapplication.core.enums.StatusNotificacao;
import com.baseapplication.core.model.superClasses.Notificacao;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "ESTADO_NOTIFICACAO")
public class EstadoNotificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario destinatario;

    @OneToOne
    @JoinColumn(name = "id_notificacao")
    private Notificacao notificacao;

    @Enumerated(EnumType.STRING)
    private StatusNotificacao statusNotificacao;

    @Column(length = 500)
    private String mensagemAdicional;
}


