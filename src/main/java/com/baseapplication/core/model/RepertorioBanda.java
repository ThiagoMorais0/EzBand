package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Musica;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "REPERTORIO_BANDA")
public class RepertorioBanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idBanda;
    @Embedded
    private Musica musica;
}
