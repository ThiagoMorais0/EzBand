package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Um lugar no palco ("Guitarra 1"), nao uma pessoa. O vinculo com usuario ou
 * membro fantasma e opcional para o mapa sobreviver a troca de integrante e
 * continuar servindo a um musico substituto -- que e justamente quem mais
 * precisa dele.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "POSICAO_PALCO")
public class PosicaoPalco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_MAPA_PALCO", nullable = false)
    private Long idMapaPalco;

    @Column(nullable = false, length = 80)
    private String rotulo;

    @Column(length = 100)
    private String instrumento;

    /** Qual figura desenhar: musico em pe, sentado, maestro. */
    @Column(nullable = false, length = 50)
    private String modelo = "musico-em-pe";

    /** Percentuais das dimensoes do palco. 0,0 = fundo a esquerda. */
    @Column(name = "POS_X", nullable = false, precision = 6, scale = 2)
    private BigDecimal posX = new BigDecimal("50.00");

    @Column(name = "POS_Y", nullable = false, precision = 6, scale = 2)
    private BigDecimal posY = new BigDecimal("50.00");

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal escala = BigDecimal.ONE;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal rotacao = BigDecimal.ZERO;

    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "ID_MEMBRO_FANTASMA")
    private Long idMembroFantasma;

    @Column(name = "BACKING_VOCAL", nullable = false)
    private Boolean backingVocal = false;

    @Column(name = "ORDEM_CANAL", nullable = false)
    private Integer ordemCanal = 0;

    @Column(length = 500)
    private String observacao;

    /** Nulo enquanto a ficha tecnica nunca foi preenchida (badge de pendencia). */
    @Column(name = "DATA_PREENCHIMENTO")
    private LocalDateTime dataPreenchimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", insertable = false, updatable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MEMBRO_FANTASMA", insertable = false, updatable = false)
    private MembroFantasma membroFantasma;
}
