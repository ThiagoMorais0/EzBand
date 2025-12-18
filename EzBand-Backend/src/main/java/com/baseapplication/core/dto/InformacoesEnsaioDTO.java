package com.baseapplication.core.dto;

import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.MembroFantasmaEvento;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.BandaDTO;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class InformacoesEnsaioDTO extends InformacoesEventoDTO {
    private BigDecimal valor;

    public InformacoesEnsaioDTO(Ensaio ensaio, MusicoEvento musicoEvento) {
        BeanUtils.copyProperties(ensaio, this);
        this.setBanda(new BandaDTO(ensaio.getBanda()));
        this.setStatus(ensaio.getStatus().getDescricao());
        if (musicoEvento != null && musicoEvento.getId() != null) {
            this.setInstrumentos(musicoEvento.getInstrumentos());
        }else{
            this.setUsuarioPertenceAoEvento(false);
        }
        this.setValor(ensaio.getValor());

        if(ensaio.getEstudio() != null){
          BeanUtils.copyProperties(ensaio.getEstudio().getEndereco(), this.getEndereco());
        } else if(ensaio.getEndereco() != null)
            BeanUtils.copyProperties(ensaio.getEndereco(), this.getEndereco());

        Hibernate.initialize(ensaio.getParticipantes());
        this.setParticipantes(ensaio.getParticipantes().stream().map(MusicoEventoDTO::new).collect(Collectors.toList()));
    }

    public InformacoesEnsaioDTO(Ensaio ensaio, MusicoEvento musicoEvento, List<MembroFantasmaEvento> membrosFantasma) {
        this(ensaio, musicoEvento);
        // Adicionar membros fantasma à lista de participantes
        if(membrosFantasma != null && !membrosFantasma.isEmpty()){
            List<MusicoEventoDTO> participantesCompletos = new ArrayList<>(this.getParticipantes());
            participantesCompletos.addAll(membrosFantasma.stream().map(MusicoEventoDTO::new).collect(Collectors.toList()));
            this.setParticipantes(participantesCompletos);
        }
    }
}
