package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TOKEN_VALIDACAO_CELULAR")
public class TokenValidacaoCelular {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 6)
    private String token;

    @Column(nullable = false, length = 20)
    private String celular;

    @Column(name = "ID_MEMBRO_FANTASMA")
    private Long idMembroFantasma;

    @Column(name = "ID_USUARIO")
    private Long idUsuario;

    @Column(name = "DATA_CRIACAO", nullable = false)
    private LocalDateTime dataCriacao;

    @Column(name = "DATA_EXPIRACAO", nullable = false)
    private LocalDateTime dataExpiracao;

    @Column(nullable = false)
    private Boolean validado = false;

    @Column(name = "DATA_VALIDACAO")
    private LocalDateTime dataValidacao;
}
