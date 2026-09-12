package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;
import java.util.List;

@Data
@Entity
@Table(name = "SHOW")
public class Show extends Evento {

    @ManyToOne
    @JoinColumn(name = "ID_LOCAL_EVENTO")
    private LocalEvento localEvento;

    private Time horarioPassagemSom;

    /**
     * Mapa de palco deste show. Nulo = usar o mapa padrao da banda, que e o caso
     * da maioria dos shows. Nullable na entidade de proposito: o NOT NULL nunca
     * se aplica aqui e o ddl-auto local nao consegue criar coluna obrigatoria em
     * tabela ja populada.
     */
    @Column(name = "ID_MAPA_PALCO")
    private Long idMapaPalco;

    private BigDecimal valorContrato;
    private Boolean isPortaria;
    private Integer porcentagemPortaria;
    private BigDecimal consumacaoPorMusico;
    private String linkIngresso;

    @OneToMany(mappedBy = "show", fetch = FetchType.LAZY)
    private List<CustoOperacional> custosOperacionais;

    public Show(){
        this.setTipoEvento(TipoEvento.SHOW);
    }
}
