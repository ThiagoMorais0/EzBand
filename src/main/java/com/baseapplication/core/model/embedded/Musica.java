package com.baseapplication.core.model.embedded;

import com.baseapplication.core.enums.Tonalidade;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

import java.sql.Time;

@Embeddable
public class Musica {
    private String titulo;
    private String artista;
    private String descricao;
    private String observacao;
    private Time duracao;
    @Enumerated(EnumType.STRING)
    private Tonalidade tonalidade;
}
