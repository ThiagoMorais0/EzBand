package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MEMBRO_FANTASMA")
public class MembroFantasma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_BANDA", nullable = false)
    private Long idBanda;

    @Column(nullable = false, length = 255)
    private String nome;

    @Column(length = 255)
    private String instrumento;

    @Column(name = "URL_FOTO", length = 1000)
    private String urlFoto;

    @Column(length = 1000)
    private String observacoes;

    @ManyToOne
    @JoinColumn(name = "ID_BANDA", insertable = false, updatable = false)
    private Banda banda;
}
