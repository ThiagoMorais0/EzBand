package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "GRADE_COLUNAS", nullable = false)
    private Integer gradeColunas = 12;

    @Column(name = "GRADE_LINHAS", nullable = false)
    private Integer gradeLinhas = 6;

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
