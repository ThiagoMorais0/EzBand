package com.baseapplication.core.model.dto.superClasses;

import com.baseapplication.core.dto.EnderecoDTO;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.utils.DateUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
@NoArgsConstructor
@Getter
@Setter
public class EventoDTO {

    private Long id;
    private Long idBanda;
    private String dataInclusao;
    private String data;
    private Time duracao;
    private Time horarioInicio;
    private String local;
    private EnderecoDTO endereco = new EnderecoDTO();
    private String observacoes;
    private String status;
    private Boolean confirmado;
    private BandaDTO banda;

    public EventoDTO(Evento evento){
        BeanUtils.copyProperties(evento, this);
        this.setData(DateUtils.localDateToString(evento.getData()));
        if(evento.getEndereco() != null){
            BeanUtils.copyProperties(evento.getEndereco(), this);
        }
        if(evento.getBanda() != null){
            BeanUtils.copyProperties(evento.getBanda(), this);
        }
        this.setDataInclusao(DateUtils.localDateToString(evento.getDataInclusao()));
        this.status = evento.getStatus().getDescricao();
        this.setIdBanda(evento.getBanda().getId());
    }
}
