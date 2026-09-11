package com.baseapplication.core.model;

import com.baseapplication.core.enums.OrigemItemPalco;
import com.baseapplication.core.enums.TipoItemPalco;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Item tecnico do mapa. Tabela unica de proposito: a diferenca entre o wedge do
 * guitarrista e o praticavel solto do palco e apenas de quem ele e --
 * idPosicao nulo significa item geral do palco (PA, mesa, tomada avulsa).
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "ITEM_MAPA_PALCO")
public class ItemMapaPalco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_MAPA_PALCO", nullable = false)
    private Long idMapaPalco;

    /** Nulo = item geral do palco, nao pertence a nenhuma posicao. */
    @Column(name = "ID_POSICAO")
    private Long idPosicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private TipoItemPalco tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrigemItemPalco origem = OrigemItemPalco.PROPRIO;

    @Column(nullable = false)
    private Integer quantidade = 1;

    @Column(length = 80)
    private String rotulo;

    @Column(name = "MARCA_MODELO", length = 150)
    private String marcaModelo;

    @Column(length = 500)
    private String observacao;

    @Column(nullable = false)
    private Integer ordem = 0;

    /** Quantos canais de mesa o item consome. Pre-preenchido pelo tipo e editavel. */
    @Column(nullable = false)
    private Integer canais = 0;

    private Integer voltagem;

    private Integer vias;

    @Column(name = "MIX_INDEPENDENTE")
    private Boolean mixIndependente;

    @Column(length = 100)
    private String ponto;

    /**
     * Nulos = "desenhe onde faz sentido" (amplificador atras do dono, pedaleira
     * nos pes, microfone a frente). Preenchidos = ajuste manual. E o que faz o
     * palco nascer montado sem tirar o controle fino de quem quer usar.
     */
    @Column(name = "POS_X", precision = 6, scale = 2)
    private BigDecimal posX;

    @Column(name = "POS_Y", precision = 6, scale = 2)
    private BigDecimal posY;

    @Column(precision = 4, scale = 2)
    private BigDecimal escala;
}
