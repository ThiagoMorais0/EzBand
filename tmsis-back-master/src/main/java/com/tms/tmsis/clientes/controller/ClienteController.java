package com.tms.tmsis.clientes.controller;

import com.tms.tmsis.clientes.model.dto.ClienteDTO;
import com.tms.tmsis.clientes.service.ClienteService;
import com.tms.tmsis.core.dto.RetornoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping("/buscarCliente.do")
    public RetornoDTO buscarCliente(@RequestParam Long cpfCnpj){
        return clienteService.buscarCliente(cpfCnpj);
    }

    @PostMapping("/cadastrarCliente.do")
    public RetornoDTO cadastrarCliente(@RequestBody ClienteDTO clienteDTO){
        return clienteService.cadastrar(clienteDTO);
    }

}
