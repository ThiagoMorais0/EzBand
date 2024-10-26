package com.baseapplication.core.model.superClasses;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;


@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_banda")
    private Banda banda;
    @Column(name = "DATA_INCLUSAO")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInclusao;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;
    private Time duracao;
    private Time horarioInicio;
    private String local;
    private String cidade;
    private String observacoes;
    @Enumerated(EnumType.STRING)
    private StatusEvento status;
    @Enumerated(EnumType.STRING)

    private TipoEvento tipoEvento;

    @OneToMany(mappedBy = "id.idEvento", fetch = FetchType.LAZY)
    private List<RepertorioEvento> repertorio;

    @OneToMany(mappedBy = "id.idUsuario", fetch = FetchType.LAZY)
    private List<MusicoEvento> participantes;

}
