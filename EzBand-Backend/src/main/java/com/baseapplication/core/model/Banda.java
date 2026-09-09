package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Endereco;
import com.baseapplication.core.model.embedded.ParametrosBanda;
import com.baseapplication.core.model.publicacao.PublicacaoBanda;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "BANDA")
public class Banda implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String descricao;
    private String categoria;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInclusao;
    @Column(length = 1000)
    private String urlLogo;
    @Column(length = 1000)
    private String urlBanner;
    // Pais de origem da banda em ISO 3166-1 alpha-2 (ex: BR). Nao confundir com endereco.pais.
    @Column(length = 2)
    private String nacionalidade;
    @Column(length = 500)
    private String instagramUrl;
    @Column(length = 500)
    private String facebookUrl;
    @Column(length = 500)
    private String youtubeUrl;
    @Embedded
    private Endereco endereco;
    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<MusicoBanda> musicos;
    @Embedded
    private ParametrosBanda parametros = new ParametrosBanda();

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<Show> shows;

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<RepertorioBanda> repertorio;

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<CondicaoOrcamento> condicoesOrcamento;

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<Ensaio> ensaios;

    @OneToMany(mappedBy = "banda", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<PublicacaoBanda> publicacoes = new ArrayList<>();

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<MembroFantasma> membrosFantasma;

    public List<Usuario> getUsuariosMusicos() {
        return musicos.stream().map(MusicoBanda::getUsuario).collect(Collectors.toList());
    }
}