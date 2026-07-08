package com.baseapplication.core.dto;

import com.baseapplication.core.model.SocioLocalEvento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SocioLocalEventoDTO {
    private Long idUsuario;
    private String nome;
    private String urlFotoPerfil;
    private Boolean aprovaShows;

    public SocioLocalEventoDTO(SocioLocalEvento entity) {
        this.idUsuario = entity.getUsuario().getId();
        this.nome = entity.getUsuario().getNome();
        this.urlFotoPerfil = entity.getUsuario().getUrlFotoPerfil();
        this.aprovaShows = entity.getAprovaShows();
    }
}
