package com.baseapplication.core.controller;

import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/evento")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/buscarPorId")
    public RetornoDTO buscar(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento){
        try{
            return RetornoDTO.success(eventoService.buscarPorId(idEvento, tipoEvento));
        }catch (Exception e){
            return RetornoDTO.error();
        }
    }

}
