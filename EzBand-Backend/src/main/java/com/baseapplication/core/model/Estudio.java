package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Endereco;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"socios", "servicos", "equipamentos", "ensaios", "avaliacoes", "diasFuncionamento"})
@Entity
@Table(name = "ESTUDIO")
public class Estudio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ID_USUARIO")
    private Usuario proprietario = new Usuario();
    @Embedded
    private Endereco endereco = new Endereco();
    private String nome;
    private String descricao;
    private LocalDateTime horarioInicioFuncionamento;
    private LocalDateTime horarioFinalFuncionamento;
    private LocalDateTime DataInclusao = LocalDateTime.now();
    private String urlFotoPerfil;
    private boolean exigirConfirmacaoEnsaios = false;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "ESTUDIO_DIAS_FUNCIONAMENTO", joinColumns = @JoinColumn(name = "ID_ESTUDIO"))
    @Column(name = "DIA")
    private List<String> diasFuncionamento = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "ESTUDIO_SOCIOS",
        joinColumns = @JoinColumn(name = "ID_ESTUDIO"),
        inverseJoinColumns = @JoinColumn(name = "ID_USUARIO")
    )
    private List<Usuario> socios = new ArrayList<>();

    @OneToMany(mappedBy = "estudio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicoEstudio> servicos = new ArrayList<>();

    @OneToMany(mappedBy = "estudio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipamentoEstudio> equipamentos = new ArrayList<>();

    @OneToMany(mappedBy = "estudio", fetch = FetchType.LAZY)
    private List<Ensaio> ensaios = new ArrayList<>();

    @OneToMany(mappedBy = "estudio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvaliacaoEstudio> avaliacoes = new ArrayList<>();

    @Transient
    public Double getMediaAvaliacoes() {
        if (avaliacoes.isEmpty()) return 0.0;
        return avaliacoes.stream()
                .mapToInt(AvaliacaoEstudio::getExperienciaGeral) // ou combinar todos os critérios
                .average()
                .orElse(0.0);
    }



}
