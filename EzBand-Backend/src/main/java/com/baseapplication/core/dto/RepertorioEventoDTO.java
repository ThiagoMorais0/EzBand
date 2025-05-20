package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.enums.Tonalidade;
import com.baseapplication.core.model.RepertorioBanda;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.RepertorioEventoId;
import com.baseapplication.core.model.embedded.Musica;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.sql.Time;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepertorioEventoDTO {
    private String titulo;
    private Integer indice;
    private String artista;
    private String descricao;
    private String observacao;
    private String duracao;
    private Tonalidade tonalidade;
    private String bloco;

    public static RepertorioEvento toEntity(RepertorioEventoDTO repertorioEventoDTO,
                                      Long idEvento,
                                      Long idBanda,
                                      TipoEvento tipoEvento){
        RepertorioEvento repertorioEvento = new RepertorioEvento();

        repertorioEvento.setId(new RepertorioEventoId(
                repertorioEventoDTO.indice,
                idEvento,
                idBanda,
                tipoEvento));

        repertorioEvento.setMusica(new Musica(
                repertorioEventoDTO.titulo,
                repertorioEventoDTO.artista,
                repertorioEventoDTO.descricao,
                repertorioEventoDTO.observacao,
                repertorioEventoDTO.duracao != null ? Time.valueOf(repertorioEventoDTO.duracao) : null,
                repertorioEventoDTO.tonalidade)
        );
        return repertorioEvento;
    }

    public RepertorioEventoDTO(RepertorioEvento entity){
        BeanUtils.copyProperties(entity.getMusica(), this);
        if(entity.getMusica().getDuracao() != null){
            this.duracao = entity.getMusica().getDuracao().toString();
        }
        this.bloco = entity.getBloco();
    }
}
