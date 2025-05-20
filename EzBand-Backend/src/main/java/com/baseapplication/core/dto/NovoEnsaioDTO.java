package com.baseapplication.core.dto;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NovoEnsaioDTO {
    private Long idBanda;
    private Long idUsuario;
    private String local;
    private String cidade;
    private String dataEnsaio;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time horarioInicio;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time duracao;
    private BigDecimal valor;
    private List<MusicoEventoDTO> musicos;

    public Ensaio toEntity(){
        Ensaio ensaio = new Ensaio();
        BeanUtils.copyProperties(this, ensaio);
        ensaio.setData(DateUtils.stringToLocalDate(this.dataEnsaio));
        ensaio.setDataInclusao(LocalDate.now());
        return ensaio;
    }
}
