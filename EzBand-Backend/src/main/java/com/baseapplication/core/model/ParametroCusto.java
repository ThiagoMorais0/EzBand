package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoCalculo;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "PARAMETRO_CUSTO")
public class ParametroCusto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_banda")
    private Banda banda = new Banda();

    private String nome; // "Combustível", "Alimentação", "Hospedagem"
    private String descricao;
    private String unidade; // "R$/km", "R$/músico", "R$/dia"
    private BigDecimal valorUnitario;

    @Enumerated(EnumType.STRING)
    private TipoCalculo tipoCalculo; // POR_KM, POR_MUSICO, POR_DIA, VALOR_FIXO
}
