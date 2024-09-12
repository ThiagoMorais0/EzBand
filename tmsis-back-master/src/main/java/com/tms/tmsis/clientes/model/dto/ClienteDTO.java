package com.tms.tmsis.clientes.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tms.tmsis.clientes.enums.TipoCliente;
import com.tms.tmsis.clientes.model.Cliente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteDTO {

    private Long cpfCnpj;
    private String nome;
    private String apelido;
    private Integer tipoCliente;
    private Long telefone;
    private String email;
    private Long identificacaoFiscal;
    private Long cep;
    private String logradouro;
    private String cidade;
    private String bairro;
    private String numero;
    private String complemento;
    private String referencia;
    private String observacao;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy", locale = "pt-BR", timezone = "Brazil/East")
    private Date dataInclusao;

    public Cliente toEntity() {
        Cliente cliente = new Cliente();
        cliente.setCpfCnpj(this.cpfCnpj);
        cliente.setNome(this.nome);
        cliente.setApelido(this.apelido);
        cliente.setTipoCliente(TipoCliente.findByCodigo(this.tipoCliente));
        cliente.setTelefone(this.telefone);
        cliente.setEmail(this.email);
        cliente.setIdentificacaoFiscal(this.identificacaoFiscal);
        cliente.setCep(this.cep);
        cliente.setLogradouro(this.logradouro);
        cliente.setCidade(this.cidade);
        cliente.setBairro(this.bairro);
        cliente.setNumero(this.numero);
        cliente.setComplemento(this.complemento);
        cliente.setReferencia(this.referencia);
        cliente.setObservacao(this.observacao);
        cliente.setDataInclusao(this.dataInclusao);
        return cliente;
    }

    public static ClienteDTO toDTO(Cliente cliente) {
        ClienteDTO dto = new ClienteDTO();
        dto.setCpfCnpj(cliente.getCpfCnpj());
        dto.setNome(cliente.getNome());
        dto.setApelido(cliente.getApelido());
        dto.setTipoCliente(cliente.getTipoCliente().getCodigo());
        dto.setTelefone(cliente.getTelefone());
        dto.setEmail(cliente.getEmail());
        dto.setIdentificacaoFiscal(cliente.getIdentificacaoFiscal());
        dto.setCep(cliente.getCep());
        dto.setLogradouro(cliente.getLogradouro());
        dto.setCidade(cliente.getCidade());
        dto.setBairro(cliente.getBairro());
        dto.setNumero(cliente.getNumero());
        dto.setComplemento(cliente.getComplemento());
        dto.setReferencia(cliente.getReferencia());
        dto.setObservacao(cliente.getObservacao());
        dto.setDataInclusao(cliente.getDataInclusao());
        return dto;
    }
}
