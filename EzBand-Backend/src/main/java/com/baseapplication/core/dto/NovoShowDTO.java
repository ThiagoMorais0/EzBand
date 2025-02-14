package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class NovoShowDTO {
    private Long idBanda;
    private Long idUsuario;
    private String local;
    private String cidade;
    private String dataShow;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time horarioInicio;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time horarioPassagemSom;
    @JsonFormat(pattern = "HH:mm:ss")
    private Time duracao;
    private BigDecimal valorContrato;
    private Integer porcentagemPortaria;
    private Boolean isPortaria;
    private List<MusicoEventoDTO> musicos;

    public Show toEntity(){
        Show show = new Show();
        BeanUtils.copyProperties(this, show);
        show.setData(DateUtils.stringToLocalDate(this.dataShow));
        show.setDataInclusao(LocalDate.now());
        return show;
    }
}
