package com.baseapplication.core.model.dto;

import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.embedded.ParametrosBanda;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Embedded;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BandaDTO {
    private Long id;
    private String nome;
    private String descricao;
    private String categoria;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "Brazil/East")
    private Date dataInclusao;
    private String urlLogo;
    private Boolean permiteEntradaPorConvite;
    private Boolean exigirAprovacaoCompromissos;
    private Boolean listarObservacaoRepertorio;

    public BandaDTO(Banda banda){
        BeanUtils.copyProperties(banda, this);
    }
}
