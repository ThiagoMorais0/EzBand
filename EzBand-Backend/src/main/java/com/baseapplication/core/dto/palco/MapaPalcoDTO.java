package com.baseapplication.core.dto.palco;

import com.baseapplication.core.model.MapaPalco;
import com.baseapplication.core.utils.DateUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Agregado completo do mapa. E o unico GET que o editor precisa: mapa, posicoes
 * com suas fichas, itens gerais do palco e o resumo ja calculado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MapaPalcoDTO {

    private Long id;
    private Long idBanda;
    private String nome;
    private String descricao;
    private Boolean padrao;
    private Long idDerivadoDe;
    private Integer gradeColunas;
    private Integer gradeLinhas;
    private String observacoes;
    private String dataAtualizacao;
    private List<PosicaoPalcoDTO> posicoes = new ArrayList<>();
    private List<ItemMapaPalcoDTO> itensGerais = new ArrayList<>();
    private ResumoTecnicoDTO resumo;

    public MapaPalcoDTO(MapaPalco mapa) {
        this.id = mapa.getId();
        this.idBanda = mapa.getIdBanda();
        this.nome = mapa.getNome();
        this.descricao = mapa.getDescricao();
        this.padrao = mapa.getPadrao();
        this.idDerivadoDe = mapa.getIdDerivadoDe();
        this.gradeColunas = mapa.getGradeColunas();
        this.gradeLinhas = mapa.getGradeLinhas();
        this.observacoes = mapa.getObservacoes();
        this.dataAtualizacao = DateUtils.localDateTimeToString(mapa.getDataAtualizacao());
    }
}
