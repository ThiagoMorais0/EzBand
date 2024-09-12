package com.tms.tmsis.clientes.service.impl;

import com.tms.tmsis.clientes.dao.ClienteDao;
import com.tms.tmsis.clientes.model.Cliente;
import com.tms.tmsis.clientes.model.dto.ClienteDTO;
import com.tms.tmsis.clientes.service.ClienteService;
import com.tms.tmsis.core.dto.RetornoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteDao clienteDao;

    @Override
    public void salvar(Cliente entity) {
        clienteDao.save(entity);
    }

    @Override
    public RetornoDTO cadastrar(ClienteDTO cliente) {
        try{
            salvar(cliente.toEntity());
            return RetornoDTO.success();
        } catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @Override
    public RetornoDTO buscarCliente(Long cpfCnpj) {
        try{
            return RetornoDTO.success(ClienteDTO.toDTO(clienteDao.findById(cpfCnpj).get()));
        }catch (Exception e){
            return RetornoDTO.error();
        }
    }
}
