package com.baseapplication.core.controller;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.Context;
import jakarta.validation.constraints.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
	public ResponseEntity<?> editarUsuarioComImagem(
			@RequestParam("usuario") String usuarioJson,
			@RequestParam(required = false) MultipartFile imagem,
			@RequestParam Boolean removerImagemDePerfil) {
		try {
			Object dto = usuarioService.editarUsuarioComImagem(usuarioJson, imagem, removerImagemDePerfil);
			return ResponseEntity.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(dto);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao salvar as alterações do perfil. Tente novamente.");
		}
	}


	@GetMapping("/verificarEmailJaCadastrado")
	public boolean verificarEmailJaCadastrado(@RequestParam String email) {

		return usuarioService.verificarEmailJaCadastrado(email);

	}

	@GetMapping("/buscarQuantidadeNotificacoes")
	public Integer buscarQuantidadeNotificacoes() {
		return 0;
	}

	@GetMapping("/enviarSolicitacaoParaIngressarBanda")
	public ResponseEntity<?> enviarSolicitacaoParaIngressarBanda(@RequestParam Long idBanda, @RequestParam String instrumento) {
		try{
			usuarioService.enviarSolicitacaoParaIngressarBanda(idBanda, instrumento);
			return ResponseEntity.ok("Solicitacao enviada com sucesso");
		}catch (Exception e){
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@GetMapping("/buscarInformacoesDoPerfilPorId")
	public InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(@RequestParam Long idUsuario) {
		return usuarioService.buscarInformacoesDoPerfilPorId(idUsuario);
	}

//	@GetMapping("/buscarNotificacoesUsuario")
//	public List<NotificacaoDTO> buscarNotificacoesUsuario() {
//		return usuarioService.buscarNotificacoesUsuario(Context.getUsuarioLogado().getId());
//	}

	@PostMapping("/reportarErro")
	public ResponseEntity<?> reportarErro(@RequestBody ReporteErroDTO dto) {
		try{
			usuarioService.reportarErro(dto.getCategoria() + ": " + dto.getMensagem());
			return ResponseEntity.ok("Erro reportado com sucesso");
		}catch (Exception e){
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@PostMapping("/enviarEmail")
	public void enviarEmail(@RequestBody EmailDTO email) {
		usuarioService.enviarEmail(email);
	}

	@GetMapping("/buscarProximosEventosDoUsuario")
	public ResponseEntity<?> buscarProximosEventosDoUsuario(){
		return usuarioService.buscarProximosEventosDoUsuario();
	}

	@GetMapping("/buscarPorEmail")
	public ResponseEntity<?> buscarPorEmail(@RequestParam String email){
		try{
			return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
		}catch (Exception e){
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@GetMapping("/buscarParticipacoesEspeciais")
	public ResponseEntity<?> buscarParticipacoesEspeciais(){
		try{
			return ResponseEntity.ok(usuarioService.buscarParticipacoesEspeciais());
		}catch (Exception e){
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@GetMapping("/buscar")
	public ResponseEntity<List<BuscaGlobalDTO>> buscarGlobal(@RequestParam("termo") String termo) {
		try {
			List<BuscaGlobalDTO> resultado = usuarioService.buscarGlobal(termo);
			return ResponseEntity.ok(resultado);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
    
    @GetMapping("/buscarBandasDoUsuario")
    public ResponseEntity<?> buscarBandasDoUsuario(){
        try{
            return ResponseEntity.ok(Context.getUsuarioLogado().getBandas().stream().map(BandaDTO::new).toList());
        }catch (Exception e){
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

	@PostMapping("/seguir")
	public ResponseEntity<?> seguirUsuario(@RequestParam Long idUsuario) {
		try {

			usuarioService.seguirUsuario(idUsuario);
			return ResponseEntity.ok("Usuário seguido com sucesso");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}

	@PostMapping("/deixarDeSeguir")
	public ResponseEntity<?> deixarDeSeguir(@RequestParam Long idUsuario) {
		try {
			usuarioService.deixarDeSeguir(idUsuario);
			return ResponseEntity.ok("Deixou de seguir o usuário");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
		}
	}
	
	@GetMapping("/buscarAmigos")
	public ResponseEntity<?> buscarAmigos(){
		try{
			return ResponseEntity.ok(usuarioService.buscarAmigos());
		}catch (Exception e){
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@GetMapping("/buscarSugestoes")
	public ResponseEntity<?> buscarSugestoes(@RequestParam String termo){
		try{
			return ResponseEntity.ok(usuarioService.buscarSugestoes(termo));
		}catch (Exception e){
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}

	@GetMapping("/buscarSeguidores")
	public ResponseEntity<?> buscarSeguidores(@RequestParam Long idUsuario){
		try{
			return ResponseEntity.ok(usuarioService.buscarSeguidores(idUsuario));
		}catch (Exception e){
			return ResponseEntity.status(500).body(e.getMessage());
		}
	}


}