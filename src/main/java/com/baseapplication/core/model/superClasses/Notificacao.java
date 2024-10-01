package com.baseapplication.core.model.superClasses;

import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Usuario;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Date;

@MappedSuperclass
public abstract class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_remetente")
    private Usuario remetente;
    private String mensagem;
    private LocalDate data;
}
