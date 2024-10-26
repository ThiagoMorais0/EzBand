package com.baseapplication.core.model.embedded;

import com.baseapplication.core.enums.Tonalidade;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
