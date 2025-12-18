package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Musica;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "REPERTORIO_BANDA")
public class RepertorioBanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private Musica musica;

    @ManyToOne
    @JoinColumn(name = "id_banda")
    private Banda banda;
    
    @Column(name = "indice")
    private Integer indice;
    
    @Column(name = "posicao_show")
    private Integer posicaoShow = 5; // 1-10: início (1-3), meio (4-7), fim (8-10)
    
    @Column(name = "energia")
    private Integer energia = 5; // 1-10: calma (1-3), moderada (4-7), agitada (8-10)
    
    @Column(name = "relevancia")
    private Integer relevancia = 5; // 1-10: baixa (1-3), média (4-7), alta (8-10)
}
