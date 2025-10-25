package com.baseapplication.core.dto;

import com.baseapplication.core.enums.PermissaoMusico;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EditarMembroMusicoBandaDTO {
    private Long idBanda;
    private Long idUsuario;
    private String instrumentos;
    private List<PermissaoMusico> permissoes;
}
