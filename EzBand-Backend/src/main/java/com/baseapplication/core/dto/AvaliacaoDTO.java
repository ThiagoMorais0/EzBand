package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoAvaliacao;
import com.baseapplication.core.model.AvaliacaoEstudio;
import com.baseapplication.core.model.AvaliacaoLocalEvento;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class AvaliacaoDTO {
    private TipoAvaliacao tipoAvaliacao;
    private int qualidadeSom = 1;
    private int estrutura = 1;
    private int organizacao = 1;
    private int atendimento = 1;
    private int experienciaGeral = 1;

    public AvaliacaoEstudio toEntityEstudio(){
        AvaliacaoEstudio avaliacao = new AvaliacaoEstudio();
        BeanUtils.copyProperties(this, avaliacao);
        return avaliacao;
    }

    public AvaliacaoLocalEvento toEntityLocalEvento(){
        AvaliacaoLocalEvento avaliacao = new AvaliacaoLocalEvento();
        BeanUtils.copyProperties(this, avaliacao);
        return avaliacao;
    }

}
