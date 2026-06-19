package com.baseapplication.core.service.impl;

import com.baseapplication.core.config.TokenService;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.DateUtils;
import com.baseapplication.core.exception.ConflictException;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.AuthenticationService;
import com.baseapplication.core.service.EmailService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.FileUtils;
import com.baseapplication.core.enums.PermissaoUsuario;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

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

	@Value("${google.client.id}")
	private String googleClientId;

	@Value("${app.cookie.secure:true}")
	private boolean cookieSecure;

	private ResponseCookie buildJwtCookie(String token) {
		return ResponseCookie.from("jwt", token)
				.httpOnly(true)
				.secure(cookieSecure)
				.sameSite("Strict")
				.path("/")
				.maxAge(Duration.ofDays(30))
				.build();
	}

	private ResponseCookie clearJwtCookie() {
		return ResponseCookie.from("jwt", "")
				.httpOnly(true)
				.secure(cookieSecure)
				.sameSite("Strict")
				.path("/")
				.maxAge(0)
				.build();
	}

	@Override
	public ResponseEntity<LoginResponseDTO> login(LoginDTO data) {
		try {
			var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
			Authentication auth = this.authenticationManager.authenticate(usernamePassword);
			var token = tokenService.gerarToken((UserDetails) auth.getPrincipal());
			if (auth.isAuthenticated()) {
				Usuario usuario = usuarioService.findByEmail(data.getEmail());
				salvarDataUltimoLogin(usuario);
				return ResponseEntity.ok()
						.header(HttpHeaders.SET_COOKIE, buildJwtCookie(token).toString())
						.body(new LoginResponseDTO(usuario));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} catch (InternalAuthenticationServiceException | BadCredentialsException | LockedException | DisabledException e) {
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
		Usuario novoUsuario = new Usuario(cadastroUsuarioDTO);
		if (cadastroUsuarioDTO.getTiposUsuario() != null && !cadastroUsuarioDTO.getTiposUsuario().isEmpty()) {
			novoUsuario.setTiposUsuario(cadastroUsuarioDTO.getTiposUsuario());
		}
		Usuario usuario = usuarioService.salvar(novoUsuario);
		if (imagem != null) {
			usuario.setUrlFotoPerfil(imagemService.saveImageAndGetUrl(imagem, "profilepictures",
					usuario.getId() + "_" + System.currentTimeMillis() + "." + FileUtils.getSufix(imagem)));
			usuarioService.salvar(usuario);
		}
		usuarioService.salvarInstrumentosRegistro(
				usuario.getId(),
				cadastroUsuarioDTO.getInstrumentos(),
				cadastroUsuarioDTO.getInstrumentoFavorito()
		);
//		emailService.enviarEmail("Novo usuário no EzBand", "O usuário " + usuario.getNome() + " acabou de se cadastrar no EzBand.", "ezband3@gmail.com");
		return ResponseEntity.ok(null);
	}

	@Override
	public ResponseEntity<?> loginComGoogle(String credential) {
		try {
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://oauth2.googleapis.com/tokeninfo?id_token=" + credential))
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() != 200) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token do Google inválido");
			}

			Map<String, String> tokenInfo = new ObjectMapper().readValue(
					response.body(), new TypeReference<Map<String, String>>() {});

			String aud = tokenInfo.get("aud");
			if (aud == null || !aud.equals(googleClientId)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token não pertence a esta aplicação");
			}

			String email = tokenInfo.get("email");
			String nome  = tokenInfo.get("name");
			String foto  = tokenInfo.get("picture");

			if (email == null || email.isBlank()) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("E-mail não disponível no token");
			}

			Usuario usuario = usuarioService.findByEmail(email);

			if (usuario == null) {
				usuario = new Usuario();
				usuario.setNome(nome != null ? nome : email);
				usuario.setEmail(email);
				usuario.setSenha(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()));
				usuario.setPermissao(PermissaoUsuario.USUARIO);
				usuario.setAtivo(true);
				usuario.setBloqueado(false);
				usuario.setUrlFotoPerfil(foto != null && !foto.isBlank() ? foto : "default");
				usuario.setDataCriacao(LocalDate.now());
				usuario.setDataUltimoLogin(LocalDateTime.now());
				usuario.setDescricao("");
				usuario.setCelularValidado(false);
				usuario.setCadastroCompleto(false);
				usuario = usuarioService.salvar(usuario);
			} else {
				salvarDataUltimoLogin(usuario);
			}

			String token = tokenService.gerarToken(usuario);
			return ResponseEntity.ok()
					.header(HttpHeaders.SET_COOKIE, buildJwtCookie(token).toString())
					.body(new LoginResponseDTO(usuario));

		} catch (Exception e) {
			log.error("Erro no login com Google: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao processar login com Google");
		}
	}

	@Override
	@Transactional
	public ResponseEntity<?> completarCadastroGoogle(CompletarCadastroGoogleDTO dto) {
		try {
			if (dto.getCelular() == null || dto.getCelular().isBlank())
				return ResponseEntity.badRequest().body("Celular é obrigatório");
			if (dto.getNascimento() == null || dto.getNascimento().isBlank())
				return ResponseEntity.badRequest().body("Data de nascimento é obrigatória");
			if (dto.getCidade() == null || dto.getCidade().isBlank())
				return ResponseEntity.badRequest().body("Cidade é obrigatória");
			if (dto.getTiposUsuario() == null || dto.getTiposUsuario().isEmpty())
				return ResponseEntity.badRequest().body("Tipo de perfil é obrigatório");

			Usuario existenteCelular = usuarioService.findByCelular(dto.getCelular());
			if (existenteCelular != null) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Celular já cadastrado");
			}

			Usuario usuario = Context.getUsuarioLogado();
			usuario.setCelular(dto.getCelular());
			usuario.setDataNascimento(DateUtils.stringToLocalDate(dto.getNascimento()));
			usuario.setCidade(dto.getCidade());
			usuario.setTiposUsuario(dto.getTiposUsuario());
			if (dto.getPais() != null)        usuario.setEndPais(dto.getPais());
			if (dto.getEstado() != null)      usuario.setEndEstado(dto.getEstado());
			if (dto.getBairro() != null)      usuario.setEndBairro(dto.getBairro());
			if (dto.getRua() != null)         usuario.setEndRua(dto.getRua());
			if (dto.getNumero() != null)      usuario.setEndNumero(dto.getNumero());
			if (dto.getCep() != null)         usuario.setEndCep(dto.getCep());
			if (dto.getComplemento() != null) usuario.setEndComplemento(dto.getComplemento());
			usuario.setCadastroCompleto(true);
			usuarioService.salvar(usuario);

			return ResponseEntity.ok(Map.of(
				"cadastroCompleto", true,
				"tiposUsuario", usuario.getTiposUsuario(),
				"celular", usuario.getCelular()
			));
		} catch (Exception e) {
			log.error("Erro ao completar cadastro Google: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao completar cadastro");
		}
	}

	@Override
	public ResponseEntity<?> logout() {
		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, clearJwtCookie().toString())
				.build();
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
