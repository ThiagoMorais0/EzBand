package com.baseapplication.core.dto;

import com.baseapplication.core.dao.MusicoEventoDao;
import com.baseapplication.core.model.MembroFantasmaEvento;
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
    private String situacao;
    private boolean isFantasma = false;

    public MusicoEventoDTO(Usuario usuario){
        this.usuario = new InfoPerfilUsuarioDTO(usuario);
    }

    public MusicoEventoDTO(MusicoEvento musicoEvento){
        this.usuario = new InfoPerfilUsuarioDTO(musicoEvento.getUsuario());
        this.instrumento = musicoEvento.getInstrumentos();
        this.cache = musicoEvento.getCache();
        this.situacao = musicoEvento.getSituacao().getDescricao();
        this.isFantasma = false;
    }

    public MusicoEventoDTO(MembroFantasmaEvento membroFantasmaEvento){
        this.usuario = new InfoPerfilUsuarioDTO(membroFantasmaEvento.getMembroFantasma());
        this.instrumento = membroFantasmaEvento.getInstrumentos();
        this.cache = membroFantasmaEvento.getCache();
        this.situacao = "Confirmado";
        this.isFantasma = true;
    }

}
