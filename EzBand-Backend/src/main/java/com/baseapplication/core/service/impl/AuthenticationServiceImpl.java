package com.baseapplication.core.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.config.TokenService;
import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioDTO;
import com.baseapplication.core.dto.LoginDTO;
import com.baseapplication.core.dto.LoginResponseDTO;
import com.baseapplication.core.exception.ConflictException;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.AuthenticationService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.FileUtils;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private TokenService tokenService;

	@Override
	public ResponseEntity<LoginResponseDTO> login(LoginDTO data) {
		try {
			var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
			Authentication auth = this.authenticationManager.authenticate(usernamePassword);

			var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

			if (auth.isAuthenticated()) {
				Usuario usuario = usuarioService.findByEmail(data.getEmail());
				return ResponseEntity.ok(new LoginResponseDTO(token, usuario));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (InternalAuthenticationServiceException | BadCredentialsException | LockedException | DisabledException e) {
			// Trate diferentes exceções de autenticação aqui, se necessário
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@Override
	public void registrar(CadastroDTO dados) {

		verificaUsuarioJaCadastrado(dados);
		dados.setSenha(criptografar(dados.getSenha()));
		usuarioService.salvar(new Usuario(dados));

	}

	private String criptografar(String texto) {
		return new BCryptPasswordEncoder().encode(texto);
	}

	private void verificaUsuarioJaCadastrado(CadastroUsuarioDTO dados) {
		if (usuarioService.findByEmail(dados.getEmail()) != null)
			throw new ConflictException("Email já cadastrado.");
	}

	private void verificaUsuarioJaCadastrado(CadastroDTO dados) {
//        if (usuarioService.findByLogin(dados.getLogin()) != null)
//            throw new ServiceException("Nome de usuário já cadastrado.");

		if (usuarioService.findByEmail(dados.getEmail()) != null)
			throw new ConflictException("Email já cadastrado.");
	}

	@Override
	public Boolean isTokenValid(String token) {
		return !tokenService.validarToken(token).equals("");
	}

	@Override
	public InfoUsuarioDTO buscarInfoUsuario(String email) {
		return InfoUsuarioDTO.toDTO(usuarioService.findByEmail(email));
	}

	@Override
	@Transactional
	public ResponseEntity<?> cadastrarUsuarioComImagem(@RequestPart("usuario") CadastroUsuarioDTO cadastroUsuarioDTO,
			@RequestPart("imagem") MultipartFile imagem) {
		System.out.println("imagem: " + imagem);
		try {
			verificaUsuarioJaCadastrado(cadastroUsuarioDTO);
		} catch (ConflictException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
		cadastroUsuarioDTO.setSenha(criptografar(cadastroUsuarioDTO.getSenha()));
		Usuario usuario = usuarioService.salvar(new Usuario(cadastroUsuarioDTO));
		usuario.setUrlFotoPerfil(imagemService.saveImageAndGetUrl(imagem, "profilepictures",
				usuario.getId() + "." + FileUtils.getSufix(imagem)));
		usuarioService.salvar(usuario);
		return ResponseEntity.ok(null);
	}
}
