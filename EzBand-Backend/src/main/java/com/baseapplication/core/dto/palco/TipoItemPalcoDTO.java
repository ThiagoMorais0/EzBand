package com.baseapplication.core.dto.palco;

import com.baseapplication.core.enums.CampoItemPalco;
import com.baseapplication.core.enums.TipoItemPalco;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Catalogo entregue ao front. E daqui que o formulario da ficha sabe que TOMADA
 * mostra voltagem e WEDGE mostra vias -- acrescentar um tipo novo e mexer so no
 * enum, sem tocar no Vue.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoItemPalcoDTO {

    private String tipo;
    private String descricao;
    private String categoria;
    private String categoriaDescricao;
    private Integer categoriaOrdem;
    private String icone;
    private Boolean desenhavel;
    private Integer canaisPadrao;
    private List<CampoDTO> campos;

    public TipoItemPalcoDTO(TipoItemPalco tipo) {
        this.tipo = tipo.name();
        this.descricao = tipo.getDescricao();
        this.categoria = tipo.getCategoria().name();
        this.categoriaDescricao = tipo.getCategoria().getDescricao();
        this.categoriaOrdem = tipo.getCategoria().getOrdem();
        this.icone = tipo.getIcone();
        this.desenhavel = tipo.getDesenhavel();
        this.canaisPadrao = tipo.getCanaisPadrao();
        this.campos = tipo.getCampos().stream().map(CampoDTO::new).collect(Collectors.toList());
    }

    @Data
    @NoArgsConstructor
    public static class CampoDTO {
        private String campo;
        private String descricao;
        private String tipoCampo;

        public CampoDTO(CampoItemPalco campo) {
            this.campo = campo.name();
            this.descricao = campo.getDescricao();
            this.tipoCampo = campo.getTipoCampo();
        }
    }
}
