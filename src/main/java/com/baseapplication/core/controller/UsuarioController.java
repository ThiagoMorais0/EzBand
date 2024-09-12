package com.baseapplication.core.controller;

import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/buscarInfoUsuarioPainel.do")
    public RetornoDTO buscarInfoUsuarioPainel(@RequestParam Long idUsuario){
        try{
            return RetornoDTO.success(usuarioService.buscarInfoPainel(idUsuario));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }
}
