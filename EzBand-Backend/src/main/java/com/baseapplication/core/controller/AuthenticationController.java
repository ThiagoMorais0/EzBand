package com.baseapplication.core.controller;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.dto.LoginDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("auth")
@EnableAutoConfiguration
@Log
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/teste")
    public String teste() {
        return "teste";
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO credenciais) {
        return authenticationService.login(credenciais);
    }

    @CrossOrigin(origins = "*")
    @PostMapping("/registrar")
    public ResponseEntity registrar(@RequestBody CadastroDTO cadastroDTO) {
        RetornoDTO retorno = authenticationService.registrar(cadastroDTO);
        if(retorno.getSuccess() == 1)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
    }


    @PostMapping("/cadastrarUsuarioComImagem")
    @CrossOrigin(origins = "*")
    public RetornoDTO cadastrarUsuarioComImagem(@RequestParam("usuario") String usuarioJson, MultipartFile imagem) throws JsonProcessingException {
        CadastroUsuarioDTO usuario = new ObjectMapper().readValue(usuarioJson, CadastroUsuarioDTO.class);

        return authenticationService.cadastrarUsuarioComImagem(usuario, imagem);
    }


    @PostMapping("/validarToken")
    public RetornoDTO validarToken(@RequestBody String token){
        Boolean tokenValido = authenticationService.isTokenValid(token);
        if(tokenValido){
            return RetornoDTO.success();
        }else{
            return RetornoDTO.error();
        }
    }

    @GetMapping("buscarInfoUsuario")
    public RetornoDTO buscarInfoUsuario(@RequestParam String email){
        return authenticationService.buscarInfoUsuario(email);
    }

}
