package com.baseapplication.core.dto;

import com.baseapplication.core.dao.MusicoEventoDao;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MusicoEventoDTO {
    private InfoPerfilUsuarioDTO usuario;
    private String instrumento = "";
    private BigDecimal cache = BigDecimal.ZERO;

    public MusicoEventoDTO(Usuario usuario){
        this.usuario = new InfoPerfilUsuarioDTO(usuario);
    }

    public MusicoEventoDTO(MusicoEvento musicoEvento){
        this.usuario = new InfoPerfilUsuarioDTO(musicoEvento.getUsuario());
        this.instrumento = musicoEvento.getInstrumentos();
        this.cache = musicoEvento.getCache();
    }

}
