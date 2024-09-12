package com.baseapplication.core.model;

import com.baseapplication.core.model.embedded.ParametrosBanda;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

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
    private Date dataInclusao;
    private String urlLogo;
    @Embedded
    private ParametrosBanda parametros;
}