package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Uma variacao completa da banda no palco: formacao + layout + equipamentos.
 * Variacoes nascem por duplicacao ("Bar pequeno" e copia de "Show completo"),
 * e idDerivadoDe guarda so a origem, para rastreio.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "MAPA_PALCO")
public class MapaPalco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_BANDA", nullable = false)
    private Long idBanda;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private Boolean padrao = false;

    @Column(name = "ID_DERIVADO_DE")
    private Long idDerivadoDe;

    /**
     * Dimensoes reais do palco. O desenho e em escala: mudar o tamanho do palco
     * da espaco para mais gente sem encolher os simbolos, que e o que permite o
     * mesmo editor servir um trio e um naipe de metais com backing vocals.
     */
    @Column(name = "LARGURA_M", nullable = false, precision = 5, scale = 2)
    private BigDecimal larguraM = new BigDecimal("8.00");

    @Column(name = "PROFUNDIDADE_M", nullable = false, precision = 5, scale = 2)
    private BigDecimal profundidadeM = new BigDecimal("6.00");

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "DATA_ATUALIZACAO", nullable = false)
    private LocalDateTime dataAtualizacao;

    @ManyToOne
    @JoinColumn(name = "ID_BANDA", insertable = false, updatable = false)
    private Banda banda;

    // Posicoes e itens NAO sao colecoes mapeadas aqui de proposito: o service
    // carrega as duas listas pelo DAO e monta o agregado explicitamente. Evita
    // o caso de o DTO sair vazio porque a colecao lazy nunca foi inicializada.
}
