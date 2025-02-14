package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "REPORTE_DE_ERRO")
public class ReporteDeErro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private String mensagem;

    public ReporteDeErro(Usuario usuario, String mensagem){
        this.usuario = usuario;
        this.mensagem = mensagem;
    }
}
