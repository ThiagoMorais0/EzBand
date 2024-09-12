package com.baseapplication.core.dto;

import com.baseapplication.core.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

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
