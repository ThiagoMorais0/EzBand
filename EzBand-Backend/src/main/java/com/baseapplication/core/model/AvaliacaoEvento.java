package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "avaliacao_evento")
public class AvaliacaoEvento {
    
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
    
    @ManyToOne
    @JoinColumn(name = "id_usuario_avaliador", nullable = false)
    private Usuario usuarioAvaliador;
    
    // Avaliação do local/estúdio (1 a 5)
    @Column(name = "qualidade_som")
    private Integer qualidadeSom;
    
    @Column(name = "estrutura")
    private Integer estrutura;
    
    @Column(name = "organizacao")
    private Integer organizacao;
    
    @Column(name = "atendimento")
    private Integer atendimento;
    
    // Avaliação geral do evento (1 a 5)
    @Column(name = "experiencia_geral", nullable = false)
    private Integer experienciaGeral;
    
    // Comentários opcionais
    @Column(name = "comentario_positivo", columnDefinition = "TEXT")
    private String comentarioPositivo;
    
    @Column(name = "comentario_negativo", columnDefinition = "TEXT")
    private String comentarioNegativo;
    
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;
    
    @Column(name = "data_avaliacao", nullable = false)
    private LocalDateTime dataAvaliacao;
}
