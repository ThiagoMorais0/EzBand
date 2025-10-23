package com.baseapplication.core.model.superClasses;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.ImagemPublicacao;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.embedded.Endereco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_publicacao", discriminatorType = DiscriminatorType.STRING)
@Table(name = "PUBLICACAO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Publicacao {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String texto;

	@Column(name = "DATA_INCLUSAO")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private LocalDateTime dataInclusao = LocalDateTime.now();

	@OneToMany(mappedBy = "publicacao", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<ImagemPublicacao> imagens;

}
