package com.tms.tmsis.foodhub.model;

import com.tms.tmsis.foodhub.dto.FHProdutoDTO;
import com.tms.tmsis.foodhub.enums.FHTipoProduto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "FH_PRODUTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FHProduto {

    @Id
    @Column(name = "CODIGO")
    private String codigo;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "DESCRICAO")
    private String descricao;

    @Column(name = "TIPO")
    private FHTipoProduto tipo;

    @Column(name = "VALOR_COMPRA")
    private BigDecimal valorCompra;

    @Column(name = "VALOR_VENDA")
    private BigDecimal valorVenda;

    @Column(name = "DATA_INCLUSAO")
    private Date dataInclusao;

    @Column(name = "DATA_ALTERACAO")
    private Date dataAlteracao;

    @Column(name = "ATIVO")
    private Boolean ativo;

    public FHProduto(String codigo, String nome, String descricao, FHTipoProduto tipo, BigDecimal valorCompra, BigDecimal valorVenda, Boolean ativo){
        this.codigo = codigo;
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.valorCompra = valorCompra;
        this.valorVenda = valorVenda;
        this.ativo = ativo;
        this.dataInclusao = new Date();
        this.dataAlteracao = new Date();
    }

    public FHProduto(FHProdutoDTO dto){
        this.codigo = dto.getCodigo();
        this.nome = dto.getNome();
        this.descricao = dto.getDescricao();
        this.tipo = FHTipoProduto.findByCodigo(dto.getTipo());
        this.valorCompra = dto.getValorCompra();
        this.valorVenda = dto.getValorVenda();
        this.ativo = dto.getAtivo();
        this.dataInclusao = new Date();
        this.dataAlteracao = new Date();
    }

}
