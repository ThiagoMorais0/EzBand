package com.baseapplication.core.dto;

import com.baseapplication.core.enums.PermissaoUsuario;
import com.baseapplication.core.model.Usuario;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InfoPerfilUsuarioDTO {
    private String nome;
    private String email;
    private String celular;
    private LocalDate dataCriacao;
    private String urlFotoPerfil;
    public InfoPerfilUsuarioDTO(Usuario usuario){
        BeanUtils.copyProperties(usuario, this);
    }
}
