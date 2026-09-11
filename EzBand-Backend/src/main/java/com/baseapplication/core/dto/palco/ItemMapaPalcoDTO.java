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
    private String modelo;
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
    private Double posX;
    private Double posY;
    private Double escala;
    private Double rotacao;

    public ItemMapaPalcoDTO(ItemMapaPalco item) {
        this.id = item.getId();
        this.idPosicao = item.getIdPosicao();
        this.tipo = item.getTipo();
        this.modelo = item.getModelo();
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
        this.posX = item.getPosX() != null ? item.getPosX().doubleValue() : null;
        this.posY = item.getPosY() != null ? item.getPosY().doubleValue() : null;
        this.escala = item.getEscala() != null ? item.getEscala().doubleValue() : null;
        this.rotacao = item.getRotacao() != null ? item.getRotacao().doubleValue() : null;
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
