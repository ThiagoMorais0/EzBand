package com.baseapplication.core.service.impl;

import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.model.MembroFantasmaEvento;
import com.baseapplication.core.model.MembroFantasmaEventoId;
import com.baseapplication.core.event.events.ConviteParaMusicoEventoEvent;
import com.baseapplication.core.event.events.SolicitacaoAgendarShowEvent;
import com.baseapplication.core.utils.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.SituacaoMusicoEvento;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.event.events.SolicitacaoAgendarEnsaioEvent;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.InvalidParamException;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.model.MembroFantasma;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.MusicoEventoId;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.EnsaioService;
import com.baseapplication.core.service.EstudioService;
import com.baseapplication.core.service.EventoService;
import com.baseapplication.core.service.LocalEventoService;
import com.baseapplication.core.service.MembroFantasmaEventoService;
import com.baseapplication.core.service.MusicoEventoService;
import com.baseapplication.core.service.NotificacaoService;
import com.baseapplication.core.service.RepertorioEventoService;
import com.baseapplication.core.service.ShowService;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.Context;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventoServiceImpl implements EventoService {

	private final ShowService showService;
	private final EnsaioService ensaioService;
	private final MusicoEventoService musicoEventoService;
	private final UsuarioService usuarioService;
	private final NotificacaoService notificacaoService;
	private final BandaService bandaService;
	private final RepertorioEventoService repertorioEventoService;
	private final EstudioService estudioService;
	private final LocalEventoService localEventoService;
	private final MembroFantasmaEventoService membroFantasmaEventoService;

	@Override
	public Evento buscarPorId(Long idEvento, TipoEvento tipoEvento) {
		switch (tipoEvento) {
		case SHOW -> {
			return showService.buscarPorId(idEvento);
		}
		case ENSAIO -> {
			return ensaioService.buscarPorId(idEvento);
		}
		default -> {
			throw new InvalidParamException("Tipo de evento inexistente");
		}
		}
	}

	@Override
	public EventosSeparadosDTO buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
		return new EventosSeparadosDTO(
				showService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario).stream().map(ShowDTO::new)
						.collect(Collectors.toList()),
				ensaioService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario).stream().map(EnsaioDTO::new)
						.collect(Collectors.toList()));
	}

	@Override
	public Evento buscarEvento(Long idEvento, TipoEvento tipoEvento) {
		switch (tipoEvento) {
		case SHOW -> {
			return showService.buscarPorId(idEvento);
		}
		case ENSAIO -> {
			return ensaioService.buscarPorId(idEvento);
		}
		default -> {
			throw new ResourceNotFoundException("Evento não encontrado");
		}
		}
	}

	@Override
	public void salvar(Evento evento) {
		switch (evento.getTipoEvento()) {
		case SHOW -> {
			showService.salvar((Show) evento);
		}
		case ENSAIO -> {
			ensaioService.salvar((Ensaio) evento);
		}
		default -> {
			throw new InvalidParamException("Tipo de evento inexistente");
		}
		}
	}

	@Override
	public InformacoesEventoDTO buscarPobuscarInformacoesEventorId(Long idEvento, TipoEvento tipoEvento) {
		Evento evento = buscarEvento(idEvento, tipoEvento);
		MusicoEvento musicoEvento = musicoEventoService.buscar(idEvento, tipoEvento,
				Context.getUsuarioLogado().getId());
		List<MembroFantasmaEvento> membrosFantasma = membroFantasmaEventoService.buscarMembrosFantasmaPorEvento(idEvento, tipoEvento);


		InformacoesEventoDTO retorno = null;

		if (evento instanceof Show)
			retorno = new InformacoesShowDTO((Show) evento, musicoEvento, membrosFantasma);
		else if (evento instanceof Ensaio)
			retorno = new InformacoesEnsaioDTO((Ensaio) evento, musicoEvento, membrosFantasma);

		if (retorno == null)
			throw new ResourceNotFoundException("Evento não encontrado");

		return retorno;
	}

	@Override
	public void atualizarInformacoesEvento(InformacoesEventoDTO informacoesEventoDTO) {
		Evento evento = buscarEvento(informacoesEventoDTO.getId(), informacoesEventoDTO.getTipoEvento());
		atualizarParticipantesEvento(evento, informacoesEventoDTO.getIdUsuariosParticipantes());
		BeanUtils.copyProperties(informacoesEventoDTO, evento);
		salvar(evento);
	}

	@Override
	public MusicoEventoDTO buscarMusicoParaEvento(String contato, TipoContato tipoContato) {
		return new MusicoEventoDTO(usuarioService.buscarPorContato(contato, tipoContato));
	}

	private void atualizarParticipantesEvento(Evento evento, List<Long> idUsuariosParticipantes) {
		removerTodosParticipantesDoEvento(evento);
		idUsuariosParticipantes.forEach(id -> {
			adicionarParticipanteEmEvento(usuarioService.buscarPorId(id), evento);
		});
	}

	private void adicionarParticipanteEmEvento(Usuario usuario, Evento evento) {
		MusicoEvento musicoEvento = new MusicoEvento();
		musicoEvento.setId(new MusicoEventoId(evento.getId(), usuario.getId(), evento.getTipoEvento()));
		// TODO: Verificar como irá receber
		musicoEvento.setInstrumentos("");
		musicoEventoService.salvar(musicoEvento);
	}

	private void removerTodosParticipantesDoEvento(Evento evento) {
		musicoEventoService.removerTodosParticipantes(evento);
	}

	@Override
	public void enviarConviteParaEvento(ConviteEventoDTO conviteEvento) {
//		notificacaoService.enviarConviteParaEvento(conviteEvento.getContato(), conviteEvento.getTipoContato(),
//				conviteEvento.getIdEvento(), conviteEvento.getTipoEvento(), conviteEvento.getIdUsuarioRemetente());
	}

	@Override
	public List<RepertorioEventoDTO> buscarRepertorioEvento(Long idEvento, TipoEvento tipoEvento) {
		//Ordernar por indice
		return buscarEvento(idEvento, tipoEvento).getRepertorio().stream().map(RepertorioEventoDTO::new)
				.sorted(Comparator.comparing(RepertorioEventoDTO::getIndice)).collect(Collectors.toList());
	}

	@Override
	public void atualizarRepertorioEvento(AtualizacaoRepertorioEventoDTO atualizacaoRepertorio) {
		Evento evento = buscarEvento(atualizacaoRepertorio.getIdEvento(), atualizacaoRepertorio.getTipoEvento());
		if (evento == null)
			throw new ResourceNotFoundException("Evento não encontrado");

		List<RepertorioEvento> repertorio = atualizacaoRepertorio.getMusicas().stream()
				.map(i -> montarMusicaRepertorioEvento(i, evento)).toList();
		repertorioEventoService.limparRepertorioEvento(evento.getId(), evento.getTipoEvento());
		repertorioEventoService.salvarLista(repertorio);

//		evento.setRepertorio(atualizacaoRepertorio.getMusicas().stream()
//				.map(musica -> RepertorioEventoDTO.toEntity(musica, atualizacaoRepertorio.getIdEvento(),
//						evento.getBanda().getId(), atualizacaoRepertorio.getTipoEvento()))
//				.collect(Collectors.toList()));
//
	}

	private RepertorioEvento montarMusicaRepertorioEvento(RepertorioEventoDTO repertorioEventoDTO, Evento evento) {
		return RepertorioEventoDTO.toEntity(repertorioEventoDTO, evento.getId(), evento.getBanda().getId(),
				evento.getTipoEvento());
	}

	@Override
	public ResponseEntity<?> buscarMembrosEDisponibilidadeParaShow(Long idBanda, LocalDate data) {
		Banda banda = bandaService.buscarPorId(idBanda);
		List<MusicoBanda> musicos = banda.getMusicos();
		List<MembroFantasma> membrosFantasma = banda.getMembrosFantasma();
		List<DisponibilidadeMusicoParaEventoDTO> musicosParaEvento = new ArrayList<>();
		
		// Adicionar músicos normais da banda
		for (MusicoBanda musico : musicos) {
//            if(isMusicoDiferenteDoUsuarioLogado(musico)){
			List<Evento> eventos = verificarDisponibilidadeEObterPossiveisEventos(musico.getId().getIdUsuario(), data);
			musicosParaEvento.add(montarDisponibilidadeMusicoDTO(musico, eventos));
//            }
		}
		
		// Adicionar membros fantasma da banda
		if (membrosFantasma != null && !membrosFantasma.isEmpty()) {
			for (MembroFantasma membroFantasma : membrosFantasma) {
				musicosParaEvento.add(montarDisponibilidadeMembroFantasmaDTO(membroFantasma));
			}
		}
		
		return ResponseEntity.ok(musicosParaEvento);
	}

	@Transactional
	@Override
	public void marcarShow(@RequestBody NovoShowDTO novoShowDTO) {
//		try {
//			System.out.println(new ObjectMapper().writeValueAsString(novoShowDTO));
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Show show = novoShowDTO.toEntity();
		Banda banda = bandaService.buscarPorId(novoShowDTO.getIdBanda());
		if (banda == null)
			throw new ResourceNotFoundException("Banda não encontrada");
		show.setBanda(banda);
		if (banda.getParametros().getExigirAprovacaoCompromissos()) {
			show.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
		} else {
			if(novoShowDTO.getIdLocalEvento() != null){
				show.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
			}else{
				show.setStatus(StatusEvento.PENDENTE);
			}
		}

		setarLocalEvento(novoShowDTO, show);
		show = showService.salvar(show);

		if (show.getLocalEvento() != null) {
			System.out.println("Publicando notificação de show");
			notificacaoService.enviarNotificacao(new SolicitacaoAgendarShowEvent(show));
		}
		incluirMusicosNoEvento(novoShowDTO.getMusicos(), banda, show, TipoEvento.SHOW);
		incluirMembrosFantasmaNoEvento(novoShowDTO.getMembrosFantasma(), show, TipoEvento.SHOW);

		boolean existeMusicoForaDaBanda = novoShowDTO.getMusicos().stream()
				.anyMatch(m -> banda.getMusicos().stream().noneMatch(i -> i.getId().getIdUsuario().equals(m.getUsuario().getId())));

		if(existeMusicoForaDaBanda){
			show.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
			showService.salvar(show);
		}
	}

	private void setarLocalEvento(NovoShowDTO novoShowDTO, Show show) {
		if (novoShowDTO.getIdLocalEvento() != null) {
			LocalEvento localEvento = localEventoService.buscarPorId(novoShowDTO.getIdLocalEvento());
			if (localEvento == null) {
				throw new InternalException("Local de evento não encontrado");
			} else {
				show.setLocalEvento(localEvento);
				show.setEndereco(localEvento.getEndereco());
			}
		}
	}

	@Transactional
	@Override
	public void marcarEnsaio(NovoEnsaioDTO novoEnsaioDTO) {
        validarDataEnsaio(novoEnsaioDTO.getDataEnsaio());
        Ensaio ensaio = novoEnsaioDTO.toEntity();
        Banda banda = bandaService.buscarPorId(novoEnsaioDTO.getIdBanda());
		ensaio.setBanda(banda);
		if (banda.getParametros().getExigirAprovacaoCompromissos()) {
			ensaio.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
		} else {
			if(novoEnsaioDTO.getIdEstudio() != null){
				ensaio.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
			}else{
				ensaio.setStatus(StatusEvento.PENDENTE);
			}
		}

		setarEstudio(novoEnsaioDTO, ensaio);
		ensaio = ensaioService.salvar(ensaio);

		if (ensaio.getEstudio() != null) {
			System.out.println("Publicando notificação");
			notificacaoService.enviarNotificacao(new SolicitacaoAgendarEnsaioEvent(ensaio));
		}
		incluirMusicosNoEvento(novoEnsaioDTO.getMusicos(), banda, ensaio, TipoEvento.ENSAIO);
		incluirMembrosFantasmaNoEvento(novoEnsaioDTO.getMembrosFantasma(), ensaio, TipoEvento.ENSAIO);

		boolean existeMusicoForaDaBanda = novoEnsaioDTO.getMusicos().stream()
				.anyMatch(m -> banda.getMusicos().stream().noneMatch(i -> i.getId().getIdUsuario().equals(m.getUsuario().getId())));

		if(existeMusicoForaDaBanda){
			ensaio.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
			ensaioService.salvar(ensaio);
		}
	}

    private void validarDataEnsaio(String dataEnsaio) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate data = LocalDate.parse(dataEnsaio, formatter);

            LocalDate hoje = LocalDate.now();
            if (data.isBefore(hoje)) {
                throw new InternalException("Data inválida! A data do ensaio não pode ser anterior ao dia atual.");
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use o formato dd/MM/yyyy.");
            // também pode lançar exceção aqui se quiser
        }
    }

    private void setarEstudio(NovoEnsaioDTO novoEnsaioDTO, Ensaio ensaio) {
		if (novoEnsaioDTO.getIdEstudio() != null) {
			Estudio estudio = estudioService.buscarPorId(novoEnsaioDTO.getIdEstudio());
			if (estudio == null) {
				throw new InternalException("Estúdio não encontrado");
			} else {
				ensaio.setEstudio(estudio);
			}
		}
	}

	@Override
	public Boolean isNotificacaoShowAceitaPorTodosMembros(Long idShow) {
//		List<Notificacao> notificacoes = notificacaoService
//				.buscarNotificacoesEventoMembros(buscarPorId(idShow, TipoEvento.SHOW));
//
//		if (notificacoes.isEmpty())
//			throw new NoContentException("Não existem notificações para este evento");
//
//		for (Notificacao notificacao : notificacoes) {
//			if (!notificacao.getStatusNotificacao().equals(StatusNotificacao.ACEITO))
//				return false;
//		}
		return true;
	}

	@Override
	public void recusarNotificacao(Long idNotificacao) {
//		notificacaoService.recusarNotificacao(idNotificacao);
	}

    @Override
    public List<Evento> buscarComDataAnteriorAHoje() {
        List<Ensaio> ensaios = ensaioService.buscarComDataAnteriorAHoje();
        List<Show> shows = showService.buscarComDataAnteriorAHoje();

        List<Evento> eventos = new ArrayList<>();
        eventos.addAll(ensaios);
        eventos.addAll(shows);
        return eventos;
    }

	@Override
	public void cancelarEvento(Long idEvento, TipoEvento tipoEvento) {
		Evento evento = buscarEvento(idEvento, tipoEvento);
		evento.setStatus(StatusEvento.CANCELADO);
		salvar(evento);
	}

	@Override
	public void atualizarMusicaRepertorio(AtualizacaoMusicaRepertorioDTO atualizacaoMusicaRepertorio) {
		RepertorioEvento repertorioEvento = repertorioEventoService.buscarPorIndiceEEvento(
				atualizacaoMusicaRepertorio.getIndice(),
				atualizacaoMusicaRepertorio.getIdEvento(),
				atualizacaoMusicaRepertorio.getTipoEvento());
		repertorioEvento.setMusica(atualizacaoMusicaRepertorio.getMusica().toEntity());
		repertorioEventoService.salvar(repertorioEvento);
	}

	@Override
	public void aceitarNotificacao(Long idNotificacao) {
//		Notificacao notificacao = notificacaoService.aceitarNotificacao(idNotificacao);
//		Evento evento = getEventoFromNotificacao(notificacao);
//		verificarSeEventoFoiAprovadoPorTodosMembros(evento);
	}

	private void verificarSeEventoFoiAprovadoPorTodosMembros(Evento evento) {
		if (isNotificacaoEventoAceitaPorTodosMembros(evento)) {
			evento.setStatus(StatusEvento.PENDENTE);
			salvar(evento);
		}
	}

	private boolean isNotificacaoEventoAceitaPorTodosMembros(Evento evento) {
//		List<Notificacao> notificacoes = notificacaoService.buscarNotificacoesEventoMembros(evento);
//
//		if (notificacoes.isEmpty())
//			throw new NoContentException("Não existem notificações para este evento");
//
//		for (Notificacao notificacao : notificacoes) {
//			if (!notificacao.getStatusNotificacao().equals(StatusNotificacao.ACEITO))
//				return false;
//		}
		return true;
	}

//	private static Evento getEventoFromNotificacao(Notificacao notificacao) {
//		Evento evento;
//		if (notificacao instanceof NotificacaoShow)
//			evento = ((NotificacaoShow) notificacao).getShow();
//		else
//			evento = ((NotificacaoEnsaio) notificacao).getEnsaio();
//		return evento;
//	}

	@Override
	public void incluirUsuarioNoEvento(Usuario usuario, Evento evento){
		MusicoEvento musicoEvento = new MusicoEvento();
		musicoEvento.setUsuario(usuario);
		musicoEvento.setEvento(evento);
		musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
		musicoEventoService.salvar(musicoEvento);
	}

	@Override
	public void incluirUsuarioNoEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuarioConvidado) {
		MusicoEvento musicoEvento = new MusicoEvento();
		musicoEvento.setUsuario(usuarioService.buscarPorId(idUsuarioConvidado));
		musicoEvento.setEvento(buscarEvento(idEvento, tipoEvento));
		musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
		musicoEventoService.salvar(musicoEvento);
	}

	@Override
	public void removerMusicaDoRepertorio(Long idEvento, TipoEvento tipoEvento, Integer indice) {
		RepertorioEvento existente = repertorioEventoService.buscarPorIndiceEEvento(indice, idEvento, tipoEvento);
		if (existente == null) {
			return;
		}
		repertorioEventoService.removerMusicaDoRepertorio(idEvento, tipoEvento, indice);
		atualizarIndices(idEvento, tipoEvento, indice);
	}

	@Override
	public void editarShow(NovoShowDTO novoShowDTO, Long idShow) {
		Show show = (Show) buscarEvento(idShow, TipoEvento.SHOW);
		Banda banda = show.getBanda();
		List<MusicoEvento> musicosAntigos = musicoEventoService.buscarMusicosPorEvento(idShow, TipoEvento.SHOW);
		List<MembroFantasmaEvento> membrosFantasmaAntigos = membroFantasmaEventoService.buscarMembrosFantasmaPorEvento(idShow, TipoEvento.SHOW);
		
		copiarPropriedadesShowDTO(novoShowDTO, show);
		VerificarHorariosNulos(novoShowDTO, show);
		atualizarMusicosEventoComVerificacao(idShow, TipoEvento.SHOW, novoShowDTO.getMusicos(), musicosAntigos, banda, show);
		atualizarMembrosFantasmaEvento(idShow, TipoEvento.SHOW, novoShowDTO.getMembrosFantasma(), membrosFantasmaAntigos);
		showService.salvar(show);
	}

	private static void copiarPropriedadesShowDTO(NovoShowDTO novoShowDTO, Show show) {
		show.setLocal(novoShowDTO.getLocal());
		show.setEndereco(novoShowDTO.getEndereco().toEntity());
		show.setData(DateUtils.stringToLocalDate(novoShowDTO.getDataShow()));
		show.setHorarioInicio(novoShowDTO.getHorarioInicio());
		show.setHorarioPassagemSom(novoShowDTO.getHorarioPassagemSom());
		show.setDuracao(novoShowDTO.getDuracao());
		show.setValorContrato(novoShowDTO.getValorContrato());
		show.setPorcentagemPortaria(novoShowDTO.getPorcentagemPortaria());
		show.setIsPortaria(novoShowDTO.getIsPortaria());
		show.setConsumacaoPorMusico(novoShowDTO.getConsumacaoPorMusico());
	}

	private static void VerificarHorariosNulos(NovoShowDTO novoShowDTO, Show show) {
		Time zero = Time.valueOf("00:00:00");
		if (novoShowDTO.getDuracao() != null && novoShowDTO.getDuracao().equals(zero))
			show.setDuracao(null);

		if (novoShowDTO.getHorarioInicio() != null && novoShowDTO.getHorarioInicio().equals(zero))
			show.setHorarioInicio(null);

		if (novoShowDTO.getHorarioPassagemSom() != null && novoShowDTO.getHorarioPassagemSom().equals(zero))
			show.setHorarioPassagemSom(null);
	}

	private void atualizarMusicosEvento(Long idEvento, TipoEvento tipoEvento, List<MusicoEventoDTO> musicos) {
		List<MusicoEvento> musicosEvento = musicoEventoService.buscarMusicosPorEvento(idEvento, tipoEvento);
		for(MusicoEvento musicoEvento : musicosEvento){
			musicoEventoService.remover(musicoEvento);
		}

		for (MusicoEventoDTO musico : musicos) {
			Usuario usuario = usuarioService.buscarPorId(musico.getUsuario().getId());
			MusicoEvento musicoEvento = new MusicoEvento();
			musicoEvento.setId(new MusicoEventoId());
			musicoEvento.setInstrumentos(musico.getInstrumento());
			BeanUtils.copyProperties(musico, musicoEvento);
			musicoEvento.getId().setIdUsuario(musico.getUsuario().getId());
			musicoEvento.getId().setIdEvento(idEvento);
			musicoEvento.getId().setTipoEvento(tipoEvento);
			musicoEvento.setUsuario(usuario);
			musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
			musicoEventoService.salvar(musicoEvento);
		}
	}

	private void atualizarMusicosEventoComVerificacao(Long idEvento, TipoEvento tipoEvento, 
			List<MusicoEventoDTO> musicosNovos, List<MusicoEvento> musicosAntigos, Banda banda, Evento evento) {
		
		Set<Long> idsAntigos = musicosAntigos.stream()
				.map(m -> m.getId().getIdUsuario())
				.collect(Collectors.toSet());
		
		for(MusicoEvento musicoEvento : musicosAntigos){
			musicoEventoService.remover(musicoEvento);
		}

		boolean temNovoMusicoExterno = false;
		
		for (MusicoEventoDTO musico : musicosNovos) {
			Usuario usuario = usuarioService.buscarPorId(musico.getUsuario().getId());
			MusicoEvento musicoEvento = new MusicoEvento();
			
			MusicoEventoId musicoEventoId = new MusicoEventoId();
			musicoEventoId.setIdEvento(idEvento);
			musicoEventoId.setTipoEvento(tipoEvento);
			musicoEventoId.setIdUsuario(usuario.getId());
			musicoEvento.setId(musicoEventoId);
			musicoEvento.setUsuario(usuario);
			musicoEvento.setInstrumentos(musico.getInstrumento());
			BeanUtils.copyProperties(musico, musicoEvento);
			
			boolean membroDaBanda = isUsuarioMembroDaBanda(usuario, banda);
			boolean jaEstavaNoEvento = idsAntigos.contains(usuario.getId());
			
			if (membroDaBanda) {
				musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
			} else {
				if (jaEstavaNoEvento) {
					musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
				} else {
					musicoEvento.setSituacao(SituacaoMusicoEvento.CONVITE_PENDENTE);
					temNovoMusicoExterno = true;
					notificacaoService.enviarNotificacao(new ConviteParaMusicoEventoEvent(
							usuario.getId(), evento, musico.getCache(), musico.getInstrumento()
					));
				}
			}
			
			musicoEventoService.salvar(musicoEvento);
		}
		
		if (temNovoMusicoExterno) {
			evento.setStatus(StatusEvento.AGUARDANDO_APROVACAO);
		}
	}

	@Override
	public void editarEnsaio(NovoEnsaioDTO novoEnsaioDTO, Long idEnsaio) {
		Ensaio ensaio = (Ensaio) buscarEvento(idEnsaio, TipoEvento.ENSAIO);
		Banda banda = ensaio.getBanda();
		List<MusicoEvento> musicosAntigos = musicoEventoService.buscarMusicosPorEvento(idEnsaio, TipoEvento.ENSAIO);
		List<MembroFantasmaEvento> membrosFantasmaAntigos = membroFantasmaEventoService.buscarMembrosFantasmaPorEvento(idEnsaio, TipoEvento.ENSAIO);
		
		ensaio.setLocal(novoEnsaioDTO.getLocal());
		ensaio.setEndereco(novoEnsaioDTO.getEndereco().toEntity());
		ensaio.setData(DateUtils.stringToLocalDate(novoEnsaioDTO.getDataEnsaio()));
		ensaio.setHorarioInicio(novoEnsaioDTO.getHorarioInicio());
		ensaio.setDuracao(novoEnsaioDTO.getDuracao());
		ensaio.setValor(novoEnsaioDTO.getValor());
		atualizarMusicosEventoComVerificacao(idEnsaio, TipoEvento.ENSAIO, novoEnsaioDTO.getMusicos(), musicosAntigos, banda, ensaio);
		atualizarMembrosFantasmaEvento(idEnsaio, TipoEvento.ENSAIO, novoEnsaioDTO.getMembrosFantasma(), membrosFantasmaAntigos);
		VerificarHorariosNulos(novoEnsaioDTO, ensaio);
		ensaioService.salvar(ensaio);
	}

	private static void VerificarHorariosNulos(NovoEnsaioDTO novoEnsaioDTO, Ensaio ensaio) {
        Time zero = Time.valueOf("00:00:00");

        if (novoEnsaioDTO.getDuracao() != null && novoEnsaioDTO.getDuracao().equals(zero))
            ensaio.setDuracao(null);

        if (novoEnsaioDTO.getHorarioInicio() != null && novoEnsaioDTO.getHorarioInicio().equals(zero))
            ensaio.setHorarioInicio(null);
    }


	private void atualizarIndices(Long idEvento, TipoEvento tipoEvento, Integer indice) {
		List<RepertorioEvento> repertorioEventos = repertorioEventoService.buscarPorEvento(idEvento, tipoEvento);
		for (RepertorioEvento repertorioEvento : repertorioEventos) {
			if (repertorioEvento.getId().getIndice() > indice) {
				repertorioEvento.getId().setIndice(repertorioEvento.getId().getIndice() - 1);
				repertorioEventoService.salvar(repertorioEvento);
			}
		}
	}

	private void incluirMusicosNoEvento(List<MusicoEventoDTO> musicos, Banda banda, Evento evento,
			TipoEvento tipoEvento) {

		for (MusicoEventoDTO musico : musicos) {
			Usuario usuario = usuarioService.buscarPorId(musico.getUsuario().getId());
			MusicoEvento musicoEvento = new MusicoEvento();
			BeanUtils.copyProperties(musico, musicoEvento);

			MusicoEventoId musicoEventoId = new MusicoEventoId();
			musicoEventoId.setIdEvento(evento.getId());
			musicoEventoId.setTipoEvento(tipoEvento);
			musicoEventoId.setIdUsuario(usuario.getId());
			musicoEvento.setId(musicoEventoId);
			musicoEvento.setUsuario(usuario);
			musicoEvento.setInstrumentos(musico.getInstrumento());

			boolean membroDaBanda = isUsuarioMembroDaBanda(usuario, banda);
			if (membroDaBanda) {
				musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
			} else {
				musicoEvento.setSituacao(SituacaoMusicoEvento.CONVITE_PENDENTE);
			}

			// Salva sempre, mesmo para convidados fora da banda
			musicoEventoService.salvar(musicoEvento);

			// Para não-membros, envia o convite via evento de notificação
			if (!membroDaBanda) {
				notificacaoService.enviarNotificacao(new ConviteParaMusicoEventoEvent(
						usuario.getId(), evento, musico.getCache(), musico.getInstrumento()
				));
			}
		}
	}

	private boolean isUsuarioMembroDaBanda(Usuario usuario, Banda banda) {
		for (MusicoBanda musicoBanda : banda.getMusicos()) {
			if (musicoBanda.getUsuario().equals(usuario))
				return true;
		}
		return false;
	}

	private ConviteEventoDTO montarConviteEvento(Usuario usuario, Evento evento, TipoEvento tipoEvento) {
		ConviteEventoDTO convite = new ConviteEventoDTO();
		convite.setContato(usuario.getEmail());
		convite.setTipoContato(TipoContato.EMAIL);
		convite.setIdUsuarioRemetente(Context.getUsuarioLogado().getId());
		convite.setTipoEvento(tipoEvento);
		convite.setIdEvento(evento.getId());
		return convite;
	}

	private static DisponibilidadeMusicoParaEventoDTO montarDisponibilidadeMusicoDTO(MusicoBanda musico,
			List<Evento> eventos) {
		DisponibilidadeMusicoParaEventoDTO dto = new DisponibilidadeMusicoParaEventoDTO();
		dto.setId(musico.getUsuario().getId());
		dto.setNome(musico.getUsuario().getNome());
		dto.setUrlFoto(musico.getUsuario().getUrlFotoPerfil());
		dto.setInstrumento(musico.getInstrumentos());
		dto.setFantasma(false);
		if (!eventos.isEmpty()) {
			dto.setDisponivel(false);
			dto.setMensagem("O músico " + musico.getUsuario().getNome() + " já tem eventos nessa data.");
			dto.setEventos(eventos.stream().map(evento -> DateUtils.localDateToString(evento.getData())  + " " + evento.getHorarioInicio().toString().substring(0, 5) + ", " + evento.getLocal()).toList());
		}
		return dto;
	}

	private static DisponibilidadeMusicoParaEventoDTO montarDisponibilidadeMembroFantasmaDTO(MembroFantasma membroFantasma) {
		DisponibilidadeMusicoParaEventoDTO dto = new DisponibilidadeMusicoParaEventoDTO();
		dto.setId(membroFantasma.getId());
		dto.setNome(membroFantasma.getNome());
		dto.setUrlFoto(membroFantasma.getUrlFoto());
		dto.setInstrumento(membroFantasma.getInstrumento());
		dto.setFantasma(true);
		dto.setDisponivel(true);
		return dto;
	}

	private List<Evento> verificarDisponibilidadeEObterPossiveisEventos(Long idUsuario, LocalDate data) {
		return showService.buscarPorUsuarioEData(idUsuario, data);
	}

	private boolean isMusicoDiferenteDoUsuarioLogado(MusicoBanda musico) {
		return !musico.getId().getIdUsuario().equals(Context.getUsuarioLogado().getId());
	}

	private boolean isMusicoDiferenteDoUsuarioLogado(MusicoEvento musico) {
		return !musico.getId().getIdUsuario().equals(Context.getUsuarioLogado().getId());
	}

	private void incluirMembrosFantasmaNoEvento(List<MembroFantasmaEventoDTO> membrosFantasma, Evento evento, TipoEvento tipoEvento) {
		if (membrosFantasma == null || membrosFantasma.isEmpty()) {
			return;
		}

		for (MembroFantasmaEventoDTO membroDTO : membrosFantasma) {
			MembroFantasmaEvento membroFantasmaEvento = new MembroFantasmaEvento();
			
			MembroFantasmaEventoId id = new MembroFantasmaEventoId();
			id.setIdEvento(evento.getId());
			id.setIdMembroFantasma(membroDTO.getIdMembroFantasma());
			id.setTipoEvento(tipoEvento);
			
			membroFantasmaEvento.setId(id);
			membroFantasmaEvento.setInstrumentos(membroDTO.getInstrumento());
			membroFantasmaEvento.setCache(membroDTO.getCache());
			
			membroFantasmaEventoService.salvar(membroFantasmaEvento);
		}
	}

	private void atualizarMembrosFantasmaEvento(Long idEvento, TipoEvento tipoEvento, 
			List<MembroFantasmaEventoDTO> membrosFantasmaNovos, List<MembroFantasmaEvento> membrosFantasmaAntigos) {
		
		// Remove todos os membros fantasma antigos
		for (MembroFantasmaEvento membroFantasmaEvento : membrosFantasmaAntigos) {
			membroFantasmaEventoService.remover(membroFantasmaEvento);
		}

		// Adiciona os novos membros fantasma
		if (membrosFantasmaNovos != null && !membrosFantasmaNovos.isEmpty()) {
			for (MembroFantasmaEventoDTO membroDTO : membrosFantasmaNovos) {
				MembroFantasmaEvento membroFantasmaEvento = new MembroFantasmaEvento();
				
				MembroFantasmaEventoId id = new MembroFantasmaEventoId();
				id.setIdEvento(idEvento);
				id.setIdMembroFantasma(membroDTO.getIdMembroFantasma());
				id.setTipoEvento(tipoEvento);
				
				membroFantasmaEvento.setId(id);
				membroFantasmaEvento.setInstrumentos(membroDTO.getInstrumento());
				membroFantasmaEvento.setCache(membroDTO.getCache());
				
				membroFantasmaEventoService.salvar(membroFantasmaEvento);
			}
		}
	}

}
