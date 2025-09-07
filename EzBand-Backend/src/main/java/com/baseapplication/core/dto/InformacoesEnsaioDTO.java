package com.baseapplication.core.dto;

import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.BandaDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Getter
@Setter
public class InformacoesEnsaioDTO extends InformacoesEventoDTO {
    private BigDecimal valor;

    public InformacoesEnsaioDTO(Ensaio ensaio, MusicoEvento musicoEvento){
        BeanUtils.copyProperties(ensaio, this);
        this.setBanda(new BandaDTO(ensaio.getBanda()));
        this.setStatus(ensaio.getStatus().getDescricao());
        this.setInstrumentos(musicoEvento.getInstrumentos());
        this.setValor(ensaio.getValor());
        BeanUtils.copyProperties(ensaio.getEndereco(), this.getEndereco());
        Hibernate.initialize(ensaio.getParticipantes());
        this.setParticipantes(ensaio.getParticipantes().stream().map(MusicoEventoDTO::new).collect(Collectors.toList()));
    }
}
