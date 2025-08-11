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
    @OneToMany(mappedBy = "estudio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServicoEstudio> servicos;

    @OneToMany(mappedBy = "estudio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipamentoEstudio> equipamentos;

}
