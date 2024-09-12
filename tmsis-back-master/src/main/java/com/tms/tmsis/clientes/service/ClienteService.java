package com.tms.tmsis.clientes.service;

import com.tms.tmsis.clientes.model.Cliente;
import com.tms.tmsis.clientes.model.dto.ClienteDTO;
import com.tms.tmsis.core.dto.RetornoDTO;

public interface ClienteService {
    void salvar(Cliente entity);

    RetornoDTO cadastrar(ClienteDTO cliente);

    RetornoDTO buscarCliente(Long cpfCnpj);
}
