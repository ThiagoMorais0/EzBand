package com.baseapplication.core.dto;

import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.BandaDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.sql.Time;

@Getter
@Setter
public class InformacoesShowDTO extends InformacoesEventoDTO {
    private Time horarioPassagemSom;
    private BigDecimal valorContrato;
    private Boolean isPortaria;
    private Integer porcentagemPortaria;

    public InformacoesShowDTO(Show show){
        BeanUtils.copyProperties(show, this);
        this.setBanda(new BandaDTO(show.getBanda()));
        this.setStatus(show.getStatus().getDescricao());
    }
}
