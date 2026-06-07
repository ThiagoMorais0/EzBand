package com.baseapplication.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

	@PostMapping("/registrar")
	public void registrar(@RequestBody CadastroDTO cadastroDTO) {
		System.out.println(cadastroDTO.getDataNascimento());
		authenticationService.registrar(cadastroDTO);
	}

	@PostMapping("/cadastrarUsuarioComImagem")
	public ResponseEntity<?> cadastrarUsuarioComImagem(@RequestParam("usuario") String usuarioJson, @RequestParam(required = false) MultipartFile imagem) {

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

	@GetMapping("/validate")
	public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
		try {
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(401)
					.body(java.util.Map.of("error", "Token não fornecido"));
			}

			String token = authHeader.substring(7);
			
			return authenticationService.validateTokenWithDetails(token);
			
		} catch (Exception e) {
			return ResponseEntity.status(401)
				.body(java.util.Map.of("error", "Token inválido ou expirado"));
		}
	}

}
