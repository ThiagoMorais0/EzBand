package com.baseapplication.core.model.dto;

import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class EnsaioDTO extends EventoDTO {
    private BigDecimal valor;

    public EnsaioDTO(Evento evento){
        Ensaio ensaio = (Ensaio) evento;
        BeanUtils.copyProperties(ensaio, this);
        if(ensaio.getEndereco() != null){
            BeanUtils.copyProperties(ensaio.getEndereco(), this.getEndereco());
        }
        this.setData(DateUtils.localDateToString(ensaio.getData()));
        this.setBanda(new BandaDTO(ensaio.getBanda()));
        this.setIdBanda(ensaio.getBanda().getId());
        this.setStatus(ensaio.getStatus().getDescricao());

    }
}
