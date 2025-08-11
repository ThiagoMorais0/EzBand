package com.baseapplication.core.controller;

import com.baseapplication.core.dto.EmailDTO;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioPainelDTO;
import com.baseapplication.core.model.dto.superClasses.NotificacaoDTO;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService usuarioService;

	@GetMapping("/buscarInfoUsuarioPainel")
	public InfoUsuarioPainelDTO buscarInfoUsuarioPainel() {
		return usuarioService.buscarInfoPainel();
	}

	@GetMapping("/buscarInformacoesDoPerfil")
	public InfoPerfilUsuarioDTO buscarInformacoesDoPerfil() {
		return new InfoPerfilUsuarioDTO(Context.getUsuarioLogado());
	}

	@PostMapping("/atualizarInformacoesPerfil")
	public void atualizarInformacoesPerfil(@RequestBody InfoPerfilUsuarioDTO infoPerfil) {

		usuarioService.atualizarInformacoesPerfil(infoPerfil);

	}

	@PostMapping("/editarUsuarioComImagem")
	public InfoPerfilUsuarioDTO editarUsuarioComImagem(@RequestParam("usuario") String usuarioJson,
			MultipartFile imagem) {

		return usuarioService.editarUsuarioComImagem(usuarioJson, imagem);

	}

	@GetMapping("/verificarEmailJaCadastrado")
	@CrossOrigin(origins = "*")
	public boolean verificarEmailJaCadastrado(@RequestParam String email) {

		return usuarioService.verificarEmailJaCadastrado(email);

	}

	@GetMapping("/buscarQuantidadeNotificacoes")
	public Integer buscarQuantidadeNotificacoes() {
		return usuarioService.buscarQuantidadeNotificacoes(Context.getUsuarioLogado().getId());
	}

	@GetMapping("/enviarSolicitacaoParaIngressarBanda")
	public void enviarSolicitacaoParaIngressarBanda(@RequestParam Long idBanda, @RequestParam String instrumento) {
		usuarioService.enviarSolicitacaoParaIngressarBanda(idBanda, Context.getUsuarioLogado().getId(), instrumento);

	}

	@GetMapping("/buscarInformacoesDoPerfilPorId")
	public InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(@RequestParam Long idUsuario) {
		return usuarioService.buscarInformacoesDoPerfilPorId(idUsuario);
	}

	@GetMapping("/buscarNotificacoesUsuario")
	public List<NotificacaoDTO> buscarNotificacoesUsuario() {
		return usuarioService.buscarNotificacoesUsuario(Context.getUsuarioLogado().getId());
	}

	@PostMapping("/reportarErro")
	public void reportarErro(@RequestBody String mensagem) {
		usuarioService.reportarErro(mensagem);
	}

	@PostMapping("/enviarEmail")
	public void enviarEmail(@RequestBody EmailDTO email) {
		usuarioService.enviarEmail(email);
	}

	@GetMapping("/buscarProximosEventosDoUsuario")
	public ResponseEntity<?> buscarProximosEventosDoUsuario(){
		return usuarioService.buscarProximosEventosDoUsuario();
	}
}