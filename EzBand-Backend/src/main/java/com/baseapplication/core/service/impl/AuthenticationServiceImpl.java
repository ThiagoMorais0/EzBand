package com.baseapplication.core.service.impl;

import com.baseapplication.core.config.TokenService;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.exception.ConflictException;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.AuthenticationService;
import com.baseapplication.core.service.EmailService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.FileUtils;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Log4j2
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

	@Autowired
	private EmailService emailService;

	@Override
	public ResponseEntity<LoginResponseDTO> login(LoginDTO data) {
		try {
			var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
			Authentication auth = this.authenticationManager.authenticate(usernamePassword);

			var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

			if (auth.isAuthenticated()) {
				Usuario usuario = usuarioService.findByEmail(data.getEmail());
				salvarDataUltimoLogin(usuario);
				return ResponseEntity.ok(new LoginResponseDTO(token, usuario));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (InternalAuthenticationServiceException | BadCredentialsException | LockedException | DisabledException e) {
			// Trate diferentes exceções de autenticação aqui, se necessário
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	private void salvarDataUltimoLogin(Usuario usuario) {
		usuario.setDataUltimoLogin(LocalDateTime.now());
		usuarioService.salvar(usuario);
	}

	@Override
	public void registrar(CadastroDTO dados) {
		verificaUsuarioJaCadastrado(dados);
		dados.setSenha(criptografar(dados.getSenha()));
		usuarioService.salvar(new Usuario(dados));
		try{
			emailService.enviarEmail("Novo usuário no EzBand", "O usuário " + dados.getNome() + " acabou de se cadastrar no EzBand.", "thiagomface@gmail.com");
		}catch(Exception e){
			log.error("Erro ao enviar email");
            log.error(e.getMessage());
		}
	}

	private String criptografar(String texto) {
		return new BCryptPasswordEncoder().encode(texto);
	}

	private void verificaUsuarioJaCadastrado(CadastroUsuarioDTO dados) {
		if (usuarioService.findByEmail(dados.getEmail()) != null)
			throw new ConflictException("Email já cadastrado.");
		
		if (dados.getCelular() != null && !dados.getCelular().isEmpty()) {
			if (usuarioService.findByCelular(dados.getCelular()) != null)
				throw new ConflictException("Celular já cadastrado.");
		}
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
		if(imagem != null){
			usuario.setUrlFotoPerfil(imagemService.saveImageAndGetUrl(imagem, "profilepictures",
					usuario.getId() + "." + FileUtils.getSufix(imagem)));
			usuarioService.salvar(usuario);
		}
//		emailService.enviarEmail("Novo usuário no EzBand", "O usuário " + usuario.getNome() + " acabou de se cadastrar no EzBand.", "ezband3@gmail.com");
		return ResponseEntity.ok(null);
	}

	@Override
	public ResponseEntity<?> validateTokenWithDetails(String token) {
		try {
			// Valida o token e extrai o email (subject)
			String email = tokenService.validarToken(token);
			
			// Se o token for inválido, retorna vazio
			if (email == null || email.isEmpty()) {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("error", "Token inválido ou expirado");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
			}
			
			// Busca o usuário no banco de dados
			Usuario usuario = usuarioService.findByEmail(email);
			
			// Verifica se o usuário ainda existe
			if (usuario == null) {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("error", "Usuário não encontrado");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
			}
			
			// Verifica se o usuário está ativo e não bloqueado
			if (!usuario.getAtivo() || usuario.getBloqueado()) {
				Map<String, String> errorResponse = new HashMap<>();
				errorResponse.put("error", "Usuário inativo ou bloqueado");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
			}
			
			// Retorna os dados do usuário
			Map<String, Object> response = new HashMap<>();
			response.put("valid", true);
			response.put("userId", usuario.getId());
			response.put("email", usuario.getEmail());
			
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", "Token inválido ou expirado");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
		}
	}
}
