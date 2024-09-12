package com.tms.tmsis.apontamento.controller;

import com.tms.tmsis.apontamento.dto.ApontamentoDTO;
import com.tms.tmsis.apontamento.service.ApontamentoService;
import com.tms.tmsis.core.dto.RetornoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apontamento")
public class ApontamentoController {

    @Autowired
    ApontamentoService apontamentoService;

    @PostMapping("/registrarApontamento.do")
    public RetornoDTO registrarApontamento(@RequestBody ApontamentoDTO apontamento){
        return apontamentoService.registrarApontamento(apontamento);
    }

    @GetMapping("/buscarApontamentosUsuario.do")
    public RetornoDTO buscarApontamentosUsuario(@RequestParam String mes){
        return null;
    }


}
