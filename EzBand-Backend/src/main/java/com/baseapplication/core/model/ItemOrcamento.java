package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "ITEM_ORCAMENTO")
public class ItemOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "id_orcamento")
    private Orcamento orcamento;
    private String nomeParametro; // snapshot do nome no momento do orçamento
    private String unidade;       // "R$/km", "R$/músico", etc.
    private BigDecimal valorUnitario;    // valor padrão do parâmetro (se aplicável)
    private BigDecimal valorCustomizado; // valor informado/calculado na hora
    private BigDecimal quantidade;
    private BigDecimal subtotal;
}
