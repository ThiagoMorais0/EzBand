package com.baseapplication.core.dto;

import com.baseapplication.core.model.RepertorioBanda;
import com.baseapplication.core.model.RepertorioEvento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
public class RepertorioBandaDTO {
    private Long id;
    private Long idBanda;
    private MusicaDTO musica = new MusicaDTO();
    private Integer indice;
    private Integer posicaoShow = 5;
    private Integer energia = 5;
    private Integer relevancia = 5;

    public RepertorioBandaDTO(RepertorioBanda repertorioBanda){
        BeanUtils.copyProperties(repertorioBanda.getMusica(), this.musica);
        this.id = repertorioBanda.getId();
        this.idBanda = repertorioBanda.getBanda().getId();
        this.musica.setTonalidade(repertorioBanda.getMusica().getTonalidade().getNumero());
        this.indice = repertorioBanda.getIndice();
        this.posicaoShow = repertorioBanda.getPosicaoShow();
        this.energia = repertorioBanda.getEnergia();
        this.relevancia = repertorioBanda.getRelevancia();
    }
}
