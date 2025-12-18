package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.PendenciaAvaliacaoEvento;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendenciaAvaliacaoEventoDTO {
    
    private Long id;
    private Long idEvento;
    private TipoEvento tipoEvento;
    private String tipoEventoDescricao;
    private Long idBanda;
    private String nomeBanda;
    private String dataEvento;
    private String localEvento;
    private Integer quantidadeAdiamentos;
    private Boolean podeAdiar;
    private LocalDateTime dataAdiamento;
    
    public PendenciaAvaliacaoEventoDTO(PendenciaAvaliacaoEvento pendencia, String localEvento) {
        this.id = pendencia.getId();
        this.idEvento = pendencia.getIdEvento();
        this.tipoEvento = pendencia.getTipoEvento();
        this.tipoEventoDescricao = pendencia.getTipoEvento().getDescricao();
        this.idBanda = pendencia.getBanda().getId();
        this.nomeBanda = pendencia.getBanda().getNome();
        this.dataEvento = DateUtils.localDateToString(pendencia.getDataEvento());
        this.localEvento = localEvento;
        this.quantidadeAdiamentos = pendencia.getQuantidadeAdiamentos();
        this.podeAdiar = pendencia.getQuantidadeAdiamentos() < 3; // Máximo de 3 adiamentos
        this.dataAdiamento = pendencia.getDataAdiamento();
    }
}
