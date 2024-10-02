package com.baseapplication.core.controller;

import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/atualizarInformacoesPerfil")
    public RetornoDTO atualizarInformacoesPerfil(@RequestBody InfoPerfilUsuarioDTO infoPerfil){
        try{
            usuarioService.atualizarInformacoesPerfil(infoPerfil);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }
}
