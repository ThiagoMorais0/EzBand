package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.ParametrosBanda;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
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
    private String urlLogo;
    @OneToMany(mappedBy = "id.idBanda", fetch = FetchType.EAGER)
    private List<MusicoBanda> musicos;
    @Embedded
    private ParametrosBanda parametros = new ParametrosBanda();

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<NotificacaoShow> notificacaoShows;

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<NotificacaoEnsaio> notificacaoEnsaios;

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<Show> shows;

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<RepertorioBanda> repertorio;

    @OneToMany(mappedBy = "banda", fetch = FetchType.EAGER)
    private List<Ensaio> ensaios;
    public List<Usuario> getUsuariosMusicos() {
        return musicos.stream().map(MusicoBanda::getUsuario).collect(Collectors.toList());
    }
}