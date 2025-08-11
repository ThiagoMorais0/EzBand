package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "EQUIPAMENTO_ESTUDIO")
public class EquipamentoEstudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ID_ESTUDIO")
    private Estudio estudio;
    private String marca;
    private String modelo;
    private String observacao;
    private Boolean ativo;
    private Integer quantidade;
}
