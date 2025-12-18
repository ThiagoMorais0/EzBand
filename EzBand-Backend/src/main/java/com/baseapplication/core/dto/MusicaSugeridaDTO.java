package com.baseapplication.core.dto;

import com.baseapplication.core.model.RepertorioBanda;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicaSugeridaDTO {
    private Long idRepertorioBanda;
    private Integer ordemSugerida;
    private MusicaDTO musica = new MusicaDTO();
    private Integer posicaoShow;
    private Integer energia;
    private Integer relevancia;
    private Double pontuacao; // Pontuação calculada pelo algoritmo
    
    public MusicaSugeridaDTO(RepertorioBanda repertorioBanda, Integer ordemSugerida, Double pontuacao) {
        this.idRepertorioBanda = repertorioBanda.getId();
        this.ordemSugerida = ordemSugerida;
        BeanUtils.copyProperties(repertorioBanda.getMusica(), this.musica);
        if (repertorioBanda.getMusica().getTonalidade() != null) {
            this.musica.setTonalidade(repertorioBanda.getMusica().getTonalidade().getNumero());
        }
        this.posicaoShow = repertorioBanda.getPosicaoShow();
        this.energia = repertorioBanda.getEnergia();
        this.relevancia = repertorioBanda.getRelevancia();
        this.pontuacao = pontuacao;
    }
}
