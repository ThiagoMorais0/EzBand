package com.baseapplication.core.dto.superClasses;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "tipoEvento"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InformacoesShowDTO.class, name = "show"),
        @JsonSubTypes.Type(value = InformacoesEnsaioDTO.class, name = "ensaio")
})
@NoArgsConstructor
public abstract class InformacoesEventoDTO {
    private Long id;
    private BandaDTO banda;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataInclusao;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate data;
    private Time duracao;
    private Time horarioInicio;
    private EnderecoDTO endereco = new EnderecoDTO();
    private String local;
    private String observacoes;
    private String status;
    private Boolean confirmado;
    private String instrumentos;
    private Boolean usuarioPertenceAoEvento = true;

    private TipoEvento tipoEvento;
    private List<RepertorioEventoDTO> repertorio;
    private List<Long> idUsuariosParticipantes;
    private List<MusicoEventoDTO> participantes;

}
