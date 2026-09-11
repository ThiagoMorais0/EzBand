package com.baseapplication.core.dto.palco;

import com.baseapplication.core.model.PosicaoPalco;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosicaoPalcoDTO {

    private Long id;
    private String rotulo;
    private String instrumento;
    private Integer coluna;
    private Integer linha;
    private Integer larguraCel;
    private Integer alturaCel;
    private Long idUsuario;
    private Long idMembroFantasma;
    private String nomeOcupante;
    private String urlFotoOcupante;
    private Boolean backingVocal;
    private Integer ordemCanal;
    private String observacao;
    private Boolean preenchida;
    private List<ItemMapaPalcoDTO> itens = new ArrayList<>();

    public PosicaoPalcoDTO(PosicaoPalco posicao) {
        this.id = posicao.getId();
        this.rotulo = posicao.getRotulo();
        this.instrumento = posicao.getInstrumento();
        this.coluna = posicao.getColuna();
        this.linha = posicao.getLinha();
        this.larguraCel = posicao.getLarguraCel();
        this.alturaCel = posicao.getAlturaCel();
        this.idUsuario = posicao.getIdUsuario();
        this.idMembroFantasma = posicao.getIdMembroFantasma();
        this.backingVocal = posicao.getBackingVocal();
        this.ordemCanal = posicao.getOrdemCanal();
        this.observacao = posicao.getObservacao();
        this.preenchida = posicao.getDataPreenchimento() != null;
    }
}
