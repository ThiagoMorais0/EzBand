package com.tms.tmsis.clientes.model;

import com.tms.tmsis.clientes.enums.TipoCliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Cliente {

    @Id
    @Column(name = "cpfcnpj")
    private Long cpfCnpj;

    @Column(name = "nome")
    private String nome;

    @Column(name = "apelido")
    private String apelido;

    @Column(name = "tipo")
    private TipoCliente tipoCliente;

    @Column(name = "telefone")
    private Long telefone;

    @Column(name = "email")
    private String email;

    @Column(name = "identificacao_fiscal")
    private Long identificacaoFiscal;

    @Column(name = "end_cep")
    private Long cep;

    @Column(name = "end_logradouro")
    private String logradouro;

    @Column(name = "end_cidade")
    private String cidade;

    @Column(name = "end_bairro")
    private String bairro;

    @Column(name = "end_numero")
    private String numero;

    @Column(name = "end_complemento")
    private String complemento;

    @Column(name = "end_referencia")
    private String referencia;

    @Column(name = "end_observacao")
    private String observacao;

    @Column(name = "data_inclusao")
    private Date dataInclusao;

}
