package com.baseapplication.core.service.impl;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.config.TokenService;
import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioDTO;
import com.baseapplication.core.dto.LoginDTO;
import com.baseapplication.core.dto.LoginResponseDTO;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.AuthenticationService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.UsuarioService;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private ImagemService imagemService;

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
		} catch (BadCredentialsException | LockedException | DisabledException e) {
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
			throw new ServiceException("Nome de usuário já cadastrado.");

		if (usuarioService.findByEmail(dados.getEmail()) != null)
			throw new ServiceException("Email já cadastrado.");
	}

	private void verificaUsuarioJaCadastrado(CadastroDTO dados) {
//        if (usuarioService.findByLogin(dados.getLogin()) != null)
//            throw new ServiceException("Nome de usuário já cadastrado.");

		if (usuarioService.findByEmail(dados.getEmail()) != null)
			throw new ServiceException("Email já cadastrado.");
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
	public void cadastrarUsuarioComImagem(CadastroUsuarioDTO usuario, MultipartFile imagem) {

		String urlImagem = imagemService.saveImageAndGetUrl(imagem);
		verificaUsuarioJaCadastrado(usuario);
		usuario.setSenha(criptografar(usuario.getSenha()));
		usuarioService.salvar(new Usuario(usuario, urlImagem));

	}
}
