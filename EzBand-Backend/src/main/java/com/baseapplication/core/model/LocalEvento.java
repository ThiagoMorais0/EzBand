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
    private String horarioCostumeiroPowerSound;
    private String horarioCostumeiroInicioShow;
    private LocalDateTime DataInclusao;

    @OneToMany(mappedBy = "localEvento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipamentoLocalEvento> equipamentos = new ArrayList<>();

    @OneToMany(mappedBy = "localEvento", fetch = FetchType.LAZY)
    private List<Show> shows = new ArrayList<>();

    @OneToMany(mappedBy = "localEvento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvaliacaoLocalEvento> avaliacoes = new ArrayList<>();

    @OneToMany(mappedBy = "localEvento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SocioLocalEvento> socios = new ArrayList<>();

    @Transient
    public Double getMediaAvaliacoes() {
        if (avaliacoes == null || avaliacoes.isEmpty()) return 0.0;
        return avaliacoes.stream()
                .mapToInt(AvaliacaoLocalEvento::getExperienciaGeral)
                .average()
                .orElse(0.0);
    }
}
