package com.baseapplication.core.dto;

import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.dto.BandaDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Getter
@Setter
public class InformacoesEnsaioDTO extends InformacoesEventoDTO {
    private BigDecimal valor;

    public InformacoesEnsaioDTO(Ensaio ensaio){
        BeanUtils.copyProperties(ensaio, this);
        this.setBanda(new BandaDTO(ensaio.getBanda()));
        this.setStatus(ensaio.getStatus().getDescricao());
    }
}
