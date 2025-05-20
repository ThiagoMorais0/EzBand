package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Musica;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "REPERTORIO_EVENTO")
public class RepertorioEvento {

    @EmbeddedId
    private RepertorioEventoId id;

    @Embedded
    private Musica musica;


    private String bloco;
}
