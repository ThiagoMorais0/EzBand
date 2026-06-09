package com.baseapplication.core.dto;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.beans.BeanUtils;

import com.baseapplication.core.model.Show;
import com.baseapplication.core.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NovoShowDTO {
	private Long idBanda;
	private Long idUsuario;
	private String local;
	private EnderecoDTO endereco = new EnderecoDTO();
	private Long idLocalEvento;
	private String dataShow;
	//Horário pode ser nulo
	@JsonFormat(pattern = "HH:mm:ss")
	private Time horarioInicio;
	@JsonFormat(pattern = "HH:mm:ss")
	private Time horarioPassagemSom;
	@JsonFormat(pattern = "HH:mm:ss")
	private Time duracao;
	private BigDecimal valorContrato;
	private Integer porcentagemPortaria;
	private Boolean isPortaria;
	private BigDecimal consumacaoPorMusico;
	private String linkIngresso;
	private List<MusicoEventoDTO> musicos = new ArrayList<>();
	private List<MembroFantasmaEventoDTO> membrosFantasma = new ArrayList<>();

	public Show toEntity() {
		Show show = new Show();
		BeanUtils.copyProperties(this, show);
		if(this.endereco != null)
			BeanUtils.copyProperties(this.endereco, show.getEndereco());
		show.setData(DateUtils.stringToLocalDate(this.dataShow));
		show.setDataInclusao(LocalDate.now());
		show.setConsumacaoPorMusico(this.consumacaoPorMusico);
		show.setLinkIngresso(this.linkIngresso);
		verificarHorariosNulos(show);

		return show;
	}

	private void verificarHorariosNulos(Show show) {
		Time zero = Time.valueOf("00:00:00");

		if (this.duracao != null && this.duracao.equals(zero))
			show.setDuracao(null);

		if (this.horarioInicio != null && this.horarioInicio.equals(zero))
			show.setHorarioInicio(null);

		if (this.horarioPassagemSom != null && this.horarioPassagemSom.equals(zero))
			show.setHorarioPassagemSom(null);
	}
}
