package com.baseapplication.core.controller;

import java.util.List;

import com.baseapplication.core.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.EventoService;
import com.baseapplication.core.utils.DateUtils;

@RestController
@RequestMapping("/evento")
public class EventoController {

	@Autowired
	private EventoService eventoService;

	@GetMapping("/buscarPorId")
	public Evento buscar(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento) {

		return eventoService.buscarPorId(idEvento, tipoEvento);

	}

	@GetMapping("/buscarInformacoesEvento")
	public InformacoesEventoDTO buscarInformacoesEvento(@RequestParam Long idEvento,
			@RequestParam TipoEvento tipoEvento) {
		return eventoService.buscarPobuscarInformacoesEventorId(idEvento, tipoEvento);

	}

	@PostMapping("/atualizarInformacoesEvento")
	public void atualizarInformacoesEvento(@RequestBody InformacoesEventoDTO informacoesEventoDTO) {
		eventoService.atualizarInformacoesEvento(informacoesEventoDTO);

	}

	@GetMapping("/buscarMusicoParaEvento")
	public MusicoEventoDTO buscarMusicoParaEvento(@RequestParam String contato, @RequestParam TipoContato tipoContato) {
		return eventoService.buscarMusicoParaEvento(contato, tipoContato);

	}

	@PostMapping("/enviarConviteParaEvento")
	public void enviarConviteParaEvento(@RequestBody ConviteEventoDTO conviteEvento) {
		eventoService.enviarConviteParaEvento(conviteEvento);

	}

	@GetMapping("/buscarRepertorioEvento")
	public List<RepertorioEventoDTO> buscarRepertorioEvento(@RequestParam Long idEvento,
			@RequestParam String tipoEvento) {
		return eventoService.buscarRepertorioEvento(idEvento, TipoEvento.valueOf(tipoEvento));

	}

	@PostMapping("/atualizarRepertorioEvento")
	public void atualizarRepertorioEvento(@RequestBody AtualizacaoRepertorioEventoDTO atualizacaoRepertorio) {
		eventoService.atualizarRepertorioEvento(atualizacaoRepertorio);
	}

	@GetMapping("/buscarMembrosEDisponibilidadeParaShow")
	public ResponseEntity<?> buscarMembrosParaShow(@RequestParam Long idBanda,
												   @RequestParam String data) {
		return eventoService.buscarMembrosEDisponibilidadeParaShow(idBanda, DateUtils.stringToLocalDate(data));
	}

	@PostMapping("/marcarShow")
	public void marcarShow(@RequestBody NovoShowDTO novoShowDTO) {
		eventoService.marcarShow(novoShowDTO);
	}

	@PostMapping("/marcarEnsaio")
	public void marcarEnsaio(@RequestBody NovoEnsaioDTO novoEnsaioDTO) {
		eventoService.marcarEnsaio(novoEnsaioDTO);
	}

	@GetMapping("/isNotificacaoShowAceitaPorTodosMembros")
	public boolean isNotificacaoShowAceitaPorTodosMembros(Long idShow) {
		return eventoService.isNotificacaoShowAceitaPorTodosMembros(idShow);
	}

	@PostMapping("/aceitarNotificacao")
	public void aceitarNotificacao(@RequestParam Long idNotificacao) {
		eventoService.aceitarNotificacao(idNotificacao);
	}

	@PostMapping("/recusarNotificacao")
	public void recusarNotificacao(@RequestParam Long idNotificacao) {
		eventoService.recusarNotificacao(idNotificacao);
	}

}
