package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "EQUIPAMENTO_LOCAL_EVENTO")
public class EquipamentoLocalEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ID_LOCAL_EVENTO")
    private LocalEvento localEvento;
    private String marca;
    private String modelo;
    private String observacao;
    private Boolean ativo;
    private Integer quantidade;
}
