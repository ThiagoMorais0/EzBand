package com.baseapplication.core.model.dto;

import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
public class ShowDTO extends EventoDTO {
    private Time horarioPassagemSom;
    private BigDecimal valorContrato;
    private Boolean isPortaria;
    private Integer porcentagemPortaria;

    public ShowDTO(Evento evento){
        Show show = (Show) evento;
        BeanUtils.copyProperties(show, this);
        if(show.getEndereco() != null){
            BeanUtils.copyProperties(show.getEndereco(), this.getEndereco());
        }
        this.setBanda(new BandaDTO(show.getBanda()));
        this.setStatus(show.getStatus().getDescricao());
        this.setData(DateUtils.localDateToString(show.getData()));
    }
}
