package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.Endereco;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "LOCAL_EVENTO")
@Data
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

}
