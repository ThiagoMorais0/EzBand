package com.tms.tmsis.core.dto;

import com.tms.tmsis.core.enums.PermissaoUsuario;
import com.tms.tmsis.core.model.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Date;

@Setter
@Getter
@Service
@NoArgsConstructor
@AllArgsConstructor
public class InfoUsuarioDTO {

    private Long id;
    private String nome;
    private String login;
    private String email;
    private Boolean ativo;
    private Boolean bloqueado;
    private String permissao;

    public static InfoUsuarioDTO toDTO(Usuario usuario){
        InfoUsuarioDTO dto = new InfoUsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setLogin(usuario.getLogin());
        dto.setEmail(usuario.getEmail());
        dto.setAtivo(usuario.getAtivo());
        dto.setBloqueado(usuario.getBloqueado());
        dto.setPermissao(usuario.getPermissao().getRole());
        return dto;
    }
}
