package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pendencia_avaliacao_evento")
public class PendenciaAvaliacaoEvento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_evento", nullable = false)
    private Long idEvento;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    private TipoEvento tipoEvento;
    
    @ManyToOne
    @JoinColumn(name = "id_banda", nullable = false)
    private Banda banda;
    
    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;
    
    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;
    
    @Column(name = "data_adiamento")
    private LocalDateTime dataAdiamento;
    
    @Column(name = "quantidade_adiamentos")
    private Integer quantidadeAdiamentos = 0;
    
    @Column(name = "avaliado")
    private Boolean avaliado = false;
    
    @Column(name = "data_avaliacao")
    private LocalDateTime dataAvaliacao;
}
