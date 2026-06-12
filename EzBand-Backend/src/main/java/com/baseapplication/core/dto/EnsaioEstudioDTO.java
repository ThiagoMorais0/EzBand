package com.baseapplication.core.dto;

import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.utils.DateUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Time;

@Getter
@Setter
@NoArgsConstructor
public class EnsaioEstudioDTO {

    private Long id;
    private String data;
    private Time horarioInicio;
    private Time duracao;
    private String status;
    private BigDecimal valor;
    private Long idEstudio;
    private EstudioResumoDTO estudio;
    private BandaDTO banda;

    public EnsaioEstudioDTO(Ensaio ensaio) {
        this.id = ensaio.getId();
        this.data = ensaio.getData() != null ? DateUtils.localDateToString(ensaio.getData()) : null;
        this.horarioInicio = ensaio.getHorarioInicio();
        this.duracao = ensaio.getDuracao();
        this.status = ensaio.getStatus() != null ? ensaio.getStatus().getDescricao() : null;
        this.valor = ensaio.getValor();
        if (ensaio.getBanda() != null) {
            this.banda = new BandaDTO(ensaio.getBanda());
        }
        if (ensaio.getEstudio() != null) {
            this.idEstudio = ensaio.getEstudio().getId();
            this.estudio = new EstudioResumoDTO(ensaio.getEstudio());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EstudioResumoDTO {
        private Long id;
        private String nome;
        private String cor; // campo reservado; null enquanto não existir no modelo

        public EstudioResumoDTO(com.baseapplication.core.model.Estudio estudio) {
            this.id = estudio.getId();
            this.nome = estudio.getNome();
        }
    }
}
