package com.baseapplication.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioDTO;
import com.baseapplication.core.dto.LoginDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.java.Log;

@RestController
@RequestMapping("auth")
@EnableAutoConfiguration
@Log
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDTO credenciais) {
		return authenticationService.login(credenciais);
	}

	@CrossOrigin(origins = "*")
	@PostMapping("/registrar")
	public void registrar(@RequestBody CadastroDTO cadastroDTO) {
		System.out.println(cadastroDTO.getDataNascimento());
		authenticationService.registrar(cadastroDTO);
	}

	@PostMapping("/cadastrarUsuarioComImagem")
	@CrossOrigin(origins = "*")
	public ResponseEntity<?> cadastrarUsuarioComImagem(@RequestParam("usuario") String usuarioJson, MultipartFile imagem) {

		CadastroUsuarioDTO usuario = null;
		try {
			usuario = new ObjectMapper().readValue(usuarioJson, CadastroUsuarioDTO.class);
		} catch (JsonProcessingException e) {
			throw new InternalException(e.getMessage());
		}

		return authenticationService.cadastrarUsuarioComImagem(usuario, imagem);
	}

	@PostMapping("/validarToken")
	public boolean validarToken(@RequestBody String token) {
		return authenticationService.isTokenValid(token);
	}

	@GetMapping("buscarInfoUsuario")
	public InfoUsuarioDTO buscarInfoUsuario(@RequestParam String email) {
		return authenticationService.buscarInfoUsuario(email);
	}

}
