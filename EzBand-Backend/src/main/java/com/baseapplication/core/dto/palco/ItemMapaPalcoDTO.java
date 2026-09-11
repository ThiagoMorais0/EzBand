package com.baseapplication.core.dto.palco;

import com.baseapplication.core.enums.OrigemItemPalco;
import com.baseapplication.core.enums.TipoItemPalco;
import com.baseapplication.core.model.ItemMapaPalco;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemMapaPalcoDTO {

    private Long id;
    private Long idPosicao;
    private TipoItemPalco tipo;
    private String tipoDescricao;
    private String categoria;
    private String icone;
    private Boolean desenhavel;
    private OrigemItemPalco origem;
    private String origemDescricao;
    private Integer quantidade;
    private String rotulo;
    private String marcaModelo;
    private String observacao;
    private Integer ordem;
    private Integer canais;
    private Integer voltagem;
    private Integer vias;
    private Boolean mixIndependente;
    private String ponto;
    private Integer coluna;
    private Integer linha;

    public ItemMapaPalcoDTO(ItemMapaPalco item) {
        this.id = item.getId();
        this.idPosicao = item.getIdPosicao();
        this.tipo = item.getTipo();
        this.tipoDescricao = item.getTipo() != null ? item.getTipo().getDescricao() : null;
        this.categoria = item.getTipo() != null ? item.getTipo().getCategoria().name() : null;
        this.icone = item.getTipo() != null ? item.getTipo().getIcone() : null;
        this.desenhavel = item.getTipo() != null ? item.getTipo().getDesenhavel() : Boolean.FALSE;
        this.origem = item.getOrigem();
        this.origemDescricao = item.getOrigem() != null ? item.getOrigem().getDescricao() : null;
        this.quantidade = item.getQuantidade();
        this.rotulo = item.getRotulo();
        this.marcaModelo = item.getMarcaModelo();
        this.observacao = item.getObservacao();
        this.ordem = item.getOrdem();
        this.canais = item.getCanais();
        this.voltagem = item.getVoltagem();
        this.vias = item.getVias();
        this.mixIndependente = item.getMixIndependente();
        this.ponto = item.getPonto();
        this.coluna = item.getColuna();
        this.linha = item.getLinha();
    }

    /** Nome curto para listas do rider: prioriza o rotulo livre, cai no tipo. */
    public String nomeExibicao() {
        if (rotulo != null && !rotulo.isBlank()) {
            return rotulo;
        }
        if (marcaModelo != null && !marcaModelo.isBlank()) {
            return tipoDescricao + " " + marcaModelo;
        }
        return tipoDescricao;
    }
}
