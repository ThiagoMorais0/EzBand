package com.baseapplication.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CompletarCadastroGoogleDTO {
    private String celular;
    private String nascimento;
    private String cidade;
    private List<String> tiposUsuario;
    private String pais;
    private String estado;
    private String bairro;
    private String rua;
    private String numero;
    private String cep;
    private String complemento;
}
