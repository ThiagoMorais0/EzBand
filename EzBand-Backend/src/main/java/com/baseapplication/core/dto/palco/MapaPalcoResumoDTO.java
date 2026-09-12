package com.baseapplication.core.dto.palco;

import com.baseapplication.core.model.MapaPalco;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Linha da tela de listagem: nao carrega posicoes nem itens. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapaPalcoResumoDTO {

    private Long id;
    private String nome;
    private String descricao;
    private Boolean padrao;
    private Integer totalPosicoes;
    private Integer posicoesPendentes;
    private String dataAtualizacao;

    public MapaPalcoResumoDTO(MapaPalco mapa) {
        this.id = mapa.getId();
        this.nome = mapa.getNome();
        this.descricao = mapa.getDescricao();
        this.padrao = mapa.getPadrao();
        this.dataAtualizacao = DateUtils.localDateTimeToString(mapa.getDataAtualizacao());
    }
}
