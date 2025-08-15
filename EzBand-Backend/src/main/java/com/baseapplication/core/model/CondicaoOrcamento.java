package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "CONDICAO_ORCAMENTO")
public class CondicaoOrcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_banda")
    private Banda banda = new Banda();

    @ManyToOne
    @JoinColumn(name = "id_orcamento")
    private Orcamento orcamento = new Orcamento();

    private String condicao;
}
