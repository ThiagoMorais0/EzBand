package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Endereco;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LOCAL_EVENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LocalEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Endereco endereco = new Endereco();

    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario proprietario = new Usuario();
    private String nome;
    private String bio;
    private String urlFotoPerfil;
    private LocalDateTime horarioInicioFuncionamento;
    private LocalDateTime horarioFinalFuncionamento;
    private LocalDateTime DataInclusao;

    @OneToMany(mappedBy = "localEvento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipamentoLocalEvento> equipamentos;

    @OneToMany(mappedBy = "localEvento", fetch = FetchType.LAZY)
    private List<Show> shows;

    @OneToMany(mappedBy = "localEvento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvaliacaoLocalEvento> avaliacoes = new ArrayList<>();

    @Transient
    public Double getMediaAvaliacoes() {
        if (avaliacoes.isEmpty()) return 0.0;
        return avaliacoes.stream()
                .mapToInt(AvaliacaoLocalEvento::getExperienciaGeral) // ou combinar todos os critérios
                .average()
                .orElse(0.0);
    }

}
