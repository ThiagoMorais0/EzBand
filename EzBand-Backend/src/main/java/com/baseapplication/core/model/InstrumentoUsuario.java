package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "instrumento_usuario")
public class InstrumentoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Boolean favorito = false;

    public InstrumentoUsuario(Long idUsuario, String nome, Boolean favorito) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.favorito = favorito;
    }
}
