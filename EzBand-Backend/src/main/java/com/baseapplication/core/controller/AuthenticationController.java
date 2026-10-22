package com.baseapplication.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.dto.CompletarCadastroGoogleDTO;
import com.baseapplication.core.dto.GoogleLoginDTO;
import com.baseapplication.core.dto.LoginDTO;
import com.baseapplication.core.exception.ExcResponse;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.AuthenticationService;
import com.baseapplication.core.utils.LimitadorTentativasLogin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;

@RestController
@RequestMapping("auth")
@EnableAutoConfiguration
@Log
public class AuthenticationController {

	@Autowired
	private AuthenticationService authenticationService;

	@Autowired
	private LimitadorTentativasLogin limitadorTentativasLogin;

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDTO credenciais, HttpServletRequest request) {
		String chaveEmail = LimitadorTentativasLogin.chaveEmail(credenciais.getEmail());
		String chaveIp = LimitadorTentativasLogin.chaveIp(request.getRemoteAddr());

		// Bloqueado nem chega a testar a senha: senão o atacante ainda descobriria a certa
		// pela resposta, mesmo durante o bloqueio
		Duration espera = limitadorTentativasLogin.tempoRestanteBloqueio(chaveEmail, chaveIp);
		if (!espera.isZero())
			return respostaMuitasTentativas(espera);

		ResponseEntity<?> resposta = authenticationService.login(credenciais);
		if (resposta.getStatusCode() == HttpStatus.UNAUTHORIZED) {
			limitadorTentativasLogin.registrarFalha(chaveEmail, LimitadorTentativasLogin.MAX_FALHAS_POR_EMAIL);
			limitadorTentativasLogin.registrarFalha(chaveIp, LimitadorTentativasLogin.MAX_FALHAS_POR_IP);
		} else if (resposta.getStatusCode().is2xxSuccessful()) {
			// Só o e-mail zera: login certo numa conta própria não pode limpar o contador do IP
			limitadorTentativasLogin.registrarSucesso(chaveEmail);
		}
		return resposta;
	}

	private static ResponseEntity<ExcResponse> respostaMuitasTentativas(Duration espera) {
		long minutos = Math.max(1, (espera.toSeconds() + 59) / 60);
		return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
				.header(HttpHeaders.RETRY_AFTER, String.valueOf(espera.toSeconds()))
				.body(new ExcResponse("TOO_MANY_REQUESTS",
						"Muitas tentativas de login. Tente novamente em " + minutos + " minuto(s)."));
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

	@PostMapping("/google")
	public ResponseEntity<?> loginComGoogle(@RequestBody GoogleLoginDTO body) {
		return authenticationService.loginComGoogle(body.getCredential());
	}

	@PostMapping("/completarCadastroGoogle")
	public ResponseEntity<?> completarCadastroGoogle(@RequestBody CompletarCadastroGoogleDTO dto) {
		return authenticationService.completarCadastroGoogle(dto);
	}

	@GetMapping("/validate")
	public ResponseEntity<?> validateToken() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()
				|| auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
			return ResponseEntity.status(401).body(java.util.Map.of("error", "Não autenticado"));
		}
		return ResponseEntity.ok(java.util.Map.of("valid", true));
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout() {
		return authenticationService.logout();
	}

}
