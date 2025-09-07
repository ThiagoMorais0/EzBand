package com.baseapplication.core.model.superClasses;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.embedded.Endereco;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED) // ou SINGLE_TABLE, dependendo do seu modelo
@Table(name = "EVENTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Evento {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "id_banda")
	private Banda banda;
	@Column(name = "DATA_INCLUSAO")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDate dataInclusao;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDate data;
	private Time duracao;
	private Time horarioInicio;
	@Embedded
	private Endereco endereco = new Endereco();
	private String local;
	private String observacoes;
	@Enumerated(EnumType.STRING)
	private StatusEvento status;

	@Enumerated(EnumType.STRING)
	private TipoEvento tipoEvento;

	@OneToMany(mappedBy = "id.idEvento", fetch = FetchType.LAZY)
	private List<RepertorioEvento> repertorio;

	@OneToMany(mappedBy = "evento", fetch = FetchType.LAZY)
	private List<MusicoEvento> participantes;

}
