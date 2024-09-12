package com.tms.tmsis.apontamento.model;

import com.tms.tmsis.apontamento.enums.TipoApontamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "APONTAMENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Apontamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "USUARIO", nullable = false)
    private String usuario;

    @Column(name = "DATA_APONTAMENTO", nullable = false)
    private Date dataApontamento;

    @Column(name = "HORARIO_APONTAMENTO", nullable = false)
    private Date horarioApontamento;

    @Column(name = "TIPO", nullable = false)
    private TipoApontamento tipo;



}