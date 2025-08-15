package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Endereco;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
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
    private LocalDateTime DataInclusao;
    private String urlFotoPerfil;
    @OneToMany(mappedBy = "estudio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicoEstudio> servicos;

    @OneToMany(mappedBy = "estudio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipamentoEstudio> equipamentos;

    @OneToMany(mappedBy = "estudio", fetch = FetchType.LAZY)
    private List<Ensaio> ensaios;

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
