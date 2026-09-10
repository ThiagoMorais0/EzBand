package com.baseapplication.core.model;

import com.baseapplication.core.enums.CategoriaDenuncia;
import com.baseapplication.core.enums.StatusDenuncia;
import com.baseapplication.core.enums.TipoAlvoDenuncia;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "denuncia")
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario_denunciante", nullable = false)
    private Usuario denunciante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alvo", nullable = false, length = 20)
    private TipoAlvoDenuncia tipoAlvo;

    // Apenas um dos dois alvos e preenchido, de acordo com o tipoAlvo.
    @ManyToOne
    @JoinColumn(name = "id_usuario_denunciado")
    private Usuario usuarioDenunciado;

    @ManyToOne
    @JoinColumn(name = "id_banda_denunciada")
    private Banda bandaDenunciada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private CategoriaDenuncia categoria;

    // Texto livre opcional que o denunciante escreve para detalhar o caso.
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusDenuncia status = StatusDenuncia.PENDENTE;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    public Denuncia(Usuario denunciante, TipoAlvoDenuncia tipoAlvo, CategoriaDenuncia categoria, String descricao) {
        this.denunciante = denunciante;
        this.tipoAlvo = tipoAlvo;
        this.categoria = categoria;
        this.descricao = descricao;
        this.status = StatusDenuncia.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }
}
