package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Endereco;
import com.baseapplication.core.model.embedded.ParametrosBanda;
import com.baseapplication.core.model.publicacao.PublicacaoBanda;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@Entity
@Table(name = "BANDA")
public class Banda {

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
    @Embedded
    private Endereco endereco;
    @OneToMany(mappedBy = "id.idBanda", fetch = FetchType.EAGER)
    private List<MusicoBanda> musicos;
    @Embedded
    private ParametrosBanda parametros = new ParametrosBanda();

//    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
//    private List<NotificacaoShow> notificacaoShows;
//
//    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
//    private List<NotificacaoEnsaio> notificacaoEnsaios;

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

    public List<Usuario> getUsuariosMusicos() {
        return musicos.stream().map(MusicoBanda::getUsuario).collect(Collectors.toList());
    }
}