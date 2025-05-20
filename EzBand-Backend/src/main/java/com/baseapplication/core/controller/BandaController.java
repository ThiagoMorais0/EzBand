package com.baseapplication.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.ConviteEventoDTO;
import com.baseapplication.core.dto.EnsaiosFuturosDTO;
import com.baseapplication.core.dto.InfoMembroBandaDTO;
import com.baseapplication.core.dto.MusicaDTO;
import com.baseapplication.core.dto.RepertorioBandaDTO;
import com.baseapplication.core.dto.ShowsFuturosDTO;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.utils.Context;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/banda")
public class BandaController {

	@Autowired
	private BandaService bandaService;

	@GetMapping("/getInfo")
	public BandaDTO getInfo(@RequestParam Long idBanda) {
		return bandaService.getInfo(idBanda);
	}

	@GetMapping("/buscarBandaParaIngressar")
	public ResponseEntity<?> buscarBandaParaIngressar(@RequestParam Long idBanda) {
		return bandaService.buscarBandaParaIngressar(idBanda);
	}

	@PostMapping("/novaBanda")
	public void novaBanda(@RequestParam("banda") String bandaJson, MultipartFile logo) {
		bandaService.novaBanda(bandaJson, logo);
	}

	@PostMapping("/cadastrarUsuario")
	public void cadastrarUsuario(@RequestParam Long idBanda, @RequestParam Long idUsuario,
			@RequestParam String instrumentos) {
		bandaService.cadastrarUsuario(idBanda, idUsuario, instrumentos);
	}

	@PostMapping("/expulsarUsuario")
	public void expulsarUsuario(@RequestParam Long idBanda, @RequestParam Long idUsuario) {
		bandaService.expulsarUsuario(idBanda, idUsuario);
	}

	@GetMapping("/getMembros")
	public List<InfoMembroBandaDTO> getMembros(@RequestParam Long idBanda) {
		return bandaService.buscarMembros(idBanda);
	}

	@PostMapping("/enviarConviteParaBanda")
	public void enviarConviteParaBanda(@RequestBody ConviteEventoDTO conviteEvento) {
		// eventoService.enviarConviteParaEvento(conviteEvento);
	}

	@GetMapping("/buscarQuantidadeDeShows")
	public Integer buscarQuantidadeDeShows(@RequestParam Long idBanda) {
		return bandaService.buscarQuantidadeDeShows(idBanda);
	}

	@GetMapping("/buscarQuantidadeDeEnsaios")
	public Integer buscarQuantidadeDeEnsaios(@RequestParam Long idBanda) {
		return bandaService.buscarQuantidadeDeEnsaios(idBanda);
	}

	@GetMapping("/buscarQuantidadeDeNotificacoes")
	public Integer buscarQuantidadeDeNotificacoes(@RequestParam Long idBanda) {

		return bandaService.buscarQuantidadeDeNotificacoes(idBanda);

	}

	@GetMapping("/buscarQuantidadeDeMembros")
	public Integer buscarQuantidadeDeMembros(@RequestParam Long idBanda) {

		return bandaService.buscarQuantidadeDeMembros(idBanda);

	}

	@GetMapping("/buscarQuantidadeDeMusicasNoRepertorio")
	public Integer buscarQuantidadeDeMusicasNoRepertorio(@RequestParam Long idBanda) {

		return bandaService.buscarQuantidadeDeMusicasNoRepertorio(idBanda);

	}

	@GetMapping("/getNivelPermissaoUsuario")
	public Integer getNivelPermissaoUsuario(@RequestParam Long idBanda) {
		return bandaService.getNivelPermissaoUsuario(idBanda, Context.getUsuarioLogado().getId());
	}

	@PostMapping("/sairDaBanda")
	public void sairDaBanda(@RequestParam Long idBanda) {
		bandaService.sairDaBanda(idBanda, Context.getUsuarioLogado().getId());
	}

	@GetMapping("/buscarShowsFuturosBanda")
	public ShowsFuturosDTO buscarShowsFuturosBanda(@RequestParam Long idBanda) {
		return bandaService.buscarShowsFuturosBanda(idBanda, Context.getUsuarioLogado().getId());
	}

	@GetMapping("/buscarEnsaiosFuturosBanda")
	public EnsaiosFuturosDTO buscarEnsaiosFuturosBanda(@RequestParam Long idBanda) {
		return bandaService.buscarEnsaiosFuturosBanda(idBanda, Context.getUsuarioLogado().getId());
	}

	@GetMapping("/buscarRepertorio")
	public List<MusicaDTO> buscarRepertorio(@RequestParam Long idBanda) {
		return bandaService.buscarRepertorio(idBanda);
	}

	@PostMapping("/adicionarMusicaAoRepertorio")
	public void adicionarMusicaAoRepertorio(@RequestBody RepertorioBandaDTO repertorioBandaDTO) {
		bandaService.adicionarMusicaAoRepertorio(repertorioBandaDTO);
	}

	@PostMapping("/editarBanda")
	public void editarBanda(@RequestParam("banda") String bandaJson, MultipartFile logo) {
		bandaService.editarBanda(bandaJson, logo);
	}

}
