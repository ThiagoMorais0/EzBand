package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "ORCAMENTO")
public class Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_evento")
    private Show evento;

    @ManyToOne
    @JoinColumn(name = "id_banda")
    private Banda banda;

    private LocalDateTime dataGeracao;
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOrcamento> itens;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CondicaoOrcamento> condicoes;
}
