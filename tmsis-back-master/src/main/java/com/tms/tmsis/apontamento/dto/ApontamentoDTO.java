package com.tms.tmsis.apontamento.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tms.tmsis.apontamento.model.Apontamento;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApontamentoDTO {

    private String usuario;

    public static Apontamento novoApontamentoToEntity(ApontamentoDTO apontamentoDTO){
        Apontamento apontamento = new Apontamento();
        apontamento.setUsuario(apontamentoDTO.getUsuario());
        apontamento.setDataApontamento(new Date());
        apontamento.setHorarioApontamento(new Date());
        return apontamento;
    }


}
