package com.baseapplication.core.controller;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.LoginDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@EnableAutoConfiguration
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/teste.do")
    public String teste() {
        return "teste";
    }

    @PostMapping("/login.do")
    public ResponseEntity login(@RequestBody LoginDTO credenciais) {
        return authenticationService.login(credenciais);
    }

    @PostMapping("/registrar.do")
    public ResponseEntity registrar(@RequestBody CadastroDTO cadastroDTO) {
        RetornoDTO retorno = authenticationService.registrar(cadastroDTO);
        if(retorno.getSuccess() == 1)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }

    @PostMapping("/validarToken.do")
    public RetornoDTO validarToken(@RequestBody String token){
        Boolean tokenValido = authenticationService.isTokenValid(token);
        if(tokenValido){
            return RetornoDTO.success();
        }else{
            return RetornoDTO.error();
        }
    }

    @GetMapping("buscarInfoUsuario.do")
    public RetornoDTO buscarInfoUsuario(@RequestParam String username){
        return authenticationService.buscarInfoUsuario(username);
    }

}
