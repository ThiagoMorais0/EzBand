package com.tms.tmsis.core.dto;

import com.tms.tmsis.core.enums.PermissaoUsuario;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CadastroDTO {

    private String nome;
    private String login;
    private String email;
    private String senha;

}
