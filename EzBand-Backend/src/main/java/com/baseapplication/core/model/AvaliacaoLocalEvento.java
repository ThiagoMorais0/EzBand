package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@Table
public class AvaliacaoLocalEvento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int qualidadeSom;       // 1 a 5
    private int estrutura;       // 1 a 5
    private int organizacao;       // 1 a 5
    private int atendimento;        // 1 a 5
    private int experienciaGeral;   // 1 a 5

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_local_evento")
    private LocalEvento localEvento;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataAvaliacao;

}
