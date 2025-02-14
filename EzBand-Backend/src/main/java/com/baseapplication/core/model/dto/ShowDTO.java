package com.baseapplication.core.model.dto;

import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

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

    public ShowDTO(Show show){
        BeanUtils.copyProperties(show, this);
        this.setData(DateUtils.localDateToString(show.getData()));
    }
}
