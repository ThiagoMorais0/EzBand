package com.baseapplication.core.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.baseapplication.core.dto.BuscaBandaDTO;
import com.baseapplication.core.dto.EditarMembroMusicoBandaDTO;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.enums.CampoMusica;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.enums.TipoRepertorio;
import com.baseapplication.core.event.events.ConviteParaUsuarioIngressarBandaEvent;
import com.baseapplication.core.event.events.UsuarioExpulsoDeBandaEvent;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.embedded.ParametrosBanda;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dao.BandaDao;
import com.baseapplication.core.dao.EnsaioDao;
import com.baseapplication.core.dao.MusicoBandaDao;
import com.baseapplication.core.dao.NotificacaoDao;
import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.model.notificacao.ConviteParaUsuarioIngressarBanda;
import com.baseapplication.core.dto.CadastroBandaDTO;
import com.baseapplication.core.dto.EdicaoBandaDTO;
import com.baseapplication.core.dto.EnsaiosFuturosDTO;
import com.baseapplication.core.dto.InfoMembroBandaDTO;
import com.baseapplication.core.dto.PropagacaoMusicaRepertorioDTO;
import com.baseapplication.core.dto.RepertorioBandaDTO;
import com.baseapplication.core.dto.ShowsFuturosDTO;
import com.baseapplication.core.enums.Tonalidade;
import com.baseapplication.core.dto.EventoConviteDTO;
import com.baseapplication.core.dto.EventoPendenteConviteDTO;
import com.baseapplication.core.exception.ConflictException;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.InvalidParamException;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.exception.RestrictionException;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.embedded.Musica;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.EventoHelperService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.ConviteBandaService;
import com.baseapplication.core.service.MusicoBandaService;
import com.baseapplication.core.service.RepertorioBandaService;
import com.baseapplication.core.utils.ChaveMusica;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Log4j2
@Service
@RequiredArgsConstructor
public class BandaServiceImpl implements BandaService {
	private final BandaDao bandaDao;
	private final MusicoBandaService musicoBandaService;
	private final ImagemService imagemService;
	private final EventoHelperService eventoHelperService;
	private final RepertorioBandaService repertorioBandaService;
	private final RepertorioEventoService repertorioEventoService;
	private final NotificacaoService notificacaoService;
	private final MembroFantasmaService membroFantasmaService;
	private final NotificacaoDao notificacaoDao;
	private final ConviteBandaService conviteBandaService;
	private final MusicoBandaDao musicoBandaDao;
	private final MusicoEventoService musicoEventoService;
	private final ShowDao showDao;
	private final EnsaioDao ensaioDao;
	private final AudioMusicaService audioMusicaService;

	/** Nenhuma banda de verdade foi fundada antes disso; abaixo daqui e erro de digitacao. */
	private static final int ANO_FUNDACAO_MINIMO = 1900;

	@Override
	public List<Banda> buscarBandasPorUsuario(Long idUsuario) {
		return bandaDao.buscarBandasPorUsuario(idUsuario).orElse(new ArrayList<Banda>());
	}

	@Override
	public List<Banda> buscarParticipacoesEspeciais(Long idUsuario) {
		return bandaDao.buscarParticipacoesEspeciais(idUsuario).orElse(new ArrayList<Banda>());
	}

	@Override
	public BandaDTO getInfo(Long idBanda) {
		return new BandaDTO(
				bandaDao.findById(idBanda).orElseThrow(() -> new ResourceNotFoundException("Banda não encontrada")),
				Context.getUsuarioLogado().getId());
	}

	@Override
	public void cadastrarUsuario(Long idBanda, Long idUsuario, String instrumentos) {
		musicoBandaService.cadastrarUsuarioEmBanda(Context.getUsuarioLogado(), bandaDao.findById(idBanda).get(),
				instrumentos, List.of(PermissaoMusico.MEMBRO_REGULAR));
	}

	@Override
	@Transactional
	public void expulsarUsuario(Long idBanda, Long idUsuario, List<EventoConviteDTO> eventosParaRemover) {
		Banda banda = buscarPorId(idBanda);
		removerMembroDosEventos(idBanda, idUsuario, eventosParaRemover);
		musicoBandaService.expulsar(idBanda, idUsuario);
		notificacaoService.enviarNotificacao(new UsuarioExpulsoDeBandaEvent(idUsuario, banda));
	}

	@Override
	@Transactional(readOnly = true)
	public List<EventoPendenteConviteDTO> buscarEventosDoMembro(Long idBanda, Long idUsuario) {
		validarMembroDaBanda(idBanda, Context.getUsuarioLogado().getId());
		return buscarEventosFuturosDoMembro(idBanda, idUsuario).stream()
				.map(EventoPendenteConviteDTO::new)
				.toList();
	}

	/**
	 * So remove dos eventos que o cliente marcou E que realmente sao eventos futuros
	 * da banda com esse musico: assim um id forjado nao tira ninguem de outro evento.
	 */
	private void removerMembroDosEventos(Long idBanda, Long idUsuario, List<EventoConviteDTO> eventosParaRemover) {
		if (eventosParaRemover == null || eventosParaRemover.isEmpty())
			return;

		for (Evento evento : buscarEventosFuturosDoMembro(idBanda, idUsuario)) {
			boolean marcado = eventosParaRemover.stream()
					.anyMatch(e -> evento.getId().equals(e.getId()) && evento.getTipoEvento() == e.getTipoEvento());
			if (marcado)
				musicoEventoService.remover(evento.getId(), evento.getTipoEvento(), idUsuario);
		}
	}

	private List<Evento> buscarEventosFuturosDoMembro(Long idBanda, Long idUsuario) {
		List<Evento> eventos = new ArrayList<>();
		eventos.addAll(showDao.buscarFuturosPorBandaEMusico(idBanda, idUsuario));
		eventos.addAll(ensaioDao.buscarFuturosPorBandaEMusico(idBanda, idUsuario));
		eventos.sort(Comparator.comparing(Evento::getData)
				.thenComparing(Evento::getHorarioInicio, Comparator.nullsLast(Comparator.naturalOrder())));
		return eventos;
	}

	private void validarMembroDaBanda(Long idBanda, Long idUsuario) {
		if (!musicoBandaDao.existsById(new MusicoBandaId(idUsuario, idBanda)))
			throw new RestrictionException("Você não faz parte desta banda.");
	}

	@Override
	@Transactional(readOnly = true)
	public List<InfoMembroBandaDTO> buscarMembros(Long idBanda) {
		List<InfoMembroBandaDTO> membros = new ArrayList<>();
		
		// Adiciona membros usuários
		membros.addAll(musicoBandaService.buscarMembrosPorIdBanda(idBanda).stream()
				.map(InfoMembroBandaDTO::new)
				.collect(Collectors.toList()));
		
		// Adiciona membros fantasma
		membros.addAll(membroFantasmaService.buscarPorIdBanda(idBanda).stream()
				.map(mf -> new InfoMembroBandaDTO(mf.toEntity()))
				.collect(Collectors.toList()));
		
		return membros;
	}

	@Transactional
	@Override
	public Long novaBanda(String bandaJson, MultipartFile logo, MultipartFile banner) {
		log.info("Nova banda: " + bandaJson);
		CadastroBandaDTO bandaDTO;
		try {
			bandaDTO = new ObjectMapper().readValue(bandaJson, CadastroBandaDTO.class);
		} catch (JsonProcessingException e) {
			throw new InternalException("Erro ao converter json");
		}

		Banda banda = bandaDao.save(new Banda());
		String urlLogo;
		if (logo != null) {
			urlLogo = imagemService.saveImageAndGetUrl(logo, "bandlogos",
					banda.getId() + "_" + System.currentTimeMillis() + "." + FileUtils.getSufix(logo));
		} else {
			urlLogo = "default";
		}

		String urlBanner = null;
		if (banner != null) {
			urlBanner = imagemService.saveImageAndGetUrl(banner, "bandbanners",
					banda.getId() + "_" + System.currentTimeMillis() + "." + FileUtils.getSufix(banner));
		}

		banda = atualizaBandaFromCadastroDTO(banda, bandaDTO, urlLogo, urlBanner);
		bandaDao.save(banda);
		cadastrarUsuarioEmBanda(bandaDTO, banda, PermissaoMusico.getAll());
		return banda.getId();
	}

	@Transactional
	@Override
	public void editarBanda(String bandaJson, MultipartFile logo, Boolean removerLogo, MultipartFile banner, Boolean removerBanner) {
		EdicaoBandaDTO bandaDTO;
		try {
			bandaDTO = new ObjectMapper().readValue(bandaJson, EdicaoBandaDTO.class);
		} catch (JsonProcessingException e) {
			throw new InternalException("Erro ao converter json");
		}

		Banda banda = buscarPorId(bandaDTO.getId());

		String urlLogo = banda.getUrlLogo();
		if (logo != null) {
			String urlLogoAntigo = urlLogo;
			urlLogo = imagemService.saveImageAndGetUrl(logo, "bandlogos",
					banda.getId() + "_" + System.currentTimeMillis() + "." + FileUtils.getSufix(logo));
			imagemService.deletarImagemPorUrl(urlLogoAntigo);
		} else if (Boolean.TRUE.equals(removerLogo)) {
			imagemService.deletarImagemPorUrl(urlLogo);
			urlLogo = "default";
		}

		String urlBanner = banda.getUrlBanner();
		if (banner != null) {
			String urlBannerAntigo = urlBanner;
			urlBanner = imagemService.saveImageAndGetUrl(banner, "bandbanners",
					banda.getId() + "_" + System.currentTimeMillis() + "." + FileUtils.getSufix(banner));
			imagemService.deletarImagemPorUrl(urlBannerAntigo);
		} else if (Boolean.TRUE.equals(removerBanner)) {
			imagemService.deletarImagemPorUrl(urlBanner);
			urlBanner = null;
		}

		setarInformacoesEditadas(banda, bandaDTO, urlLogo, urlBanner);
		bandaDao.save(banda);
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getPermissoesMusico(Long idBanda, Long idUsuario) {
		MusicoBanda musicoBanda = musicoBandaService.buscarPorIdUsuarioEIdBanda(idUsuario, idBanda);
		if (musicoBanda == null) {
			throw new InternalException("Músico não encontrado");
		}
		return musicoBanda.getPermissoes().stream().map(Enum::toString).toList();
	}

	@Override
	public void enviarConviteParaUsuarioIngressarBanda(Long idBanda, Long idUsuarioConvidado, List<EventoConviteDTO> eventos) {
		Banda banda = buscarPorId(idBanda);
		String eventosSerializados = conviteBandaService.serializarEventos(idBanda, eventos);

		// Convite em aberto vira atualização: a notificação duplicada seria descartada e os
		// eventos recém-escolhidos se perderiam.
		if (conviteBandaService.atualizarConvitePendente(idBanda, idUsuarioConvidado, eventosSerializados)) {
			return;
		}

		notificacaoService.enviarNotificacao(
				new ConviteParaUsuarioIngressarBandaEvent(idUsuarioConvidado, banda, eventosSerializados));
	}

	@Override
	public List<EnsaioDTO> buscarEnsaios(Long idBanda) {
		return buscarPorId(idBanda).getEnsaios().stream()
				.filter(ensaio -> ensaio.getData() != null) // Filtra ensaios sem data
				.sorted(Comparator.comparing(Ensaio::getData)
						.thenComparing(Ensaio::getHorarioInicio, Comparator.nullsLast(Comparator.naturalOrder())))
				.map(EnsaioDTO::new).toList();
	}

	@Override
	public List<ShowDTO> buscarShows(Long idBanda) {
		//Ordenar pela data, do mais recente para o mais longe e depois pelo horário, mesma lógica
		return buscarPorId(idBanda).getShows().stream()
				.filter(show -> show.getData() != null) // Filtra shows sem data
				.sorted(
						Comparator.comparing(Show::getData)
								.thenComparing(Show::getHorarioInicio, Comparator.nullsLast(Comparator.naturalOrder()))
				)
				.map(ShowDTO::new)
				.toList();
	}

	@Override
	public PropagacaoMusicaRepertorioDTO atualizarMusicaRertorio(RepertorioBandaDTO repertorioBandaDTO) {
		RepertorioBanda musicaRepertorio = repertorioBandaService.buscarPorId(repertorioBandaDTO.getId());

		// O VS e achado por titulo|artista normalizado, nao por FK -- ver ChaveMusica. Guardar
		// os valores antigos antes de sobrescrever e o que permite levar o audio junto quando
		// a musica e renomeada; sem isso ele fica orfao e some da tela sem erro nenhum.
		String tituloAnterior = musicaRepertorio.getMusica().getTitulo();
		String artistaAnterior = musicaRepertorio.getMusica().getArtista();

		// O embutido e mutado campo a campo logo abaixo, entao a foto do estado anterior tem que
		// sair daqui -- e o que diz, depois, o que exatamente mudou para oferecer aos shows.
		Musica anterior = new Musica();
		BeanUtils.copyProperties(musicaRepertorio.getMusica(), anterior);

		musicaRepertorio.getMusica().setTitulo(repertorioBandaDTO.getMusica().getTitulo());
		musicaRepertorio.getMusica().setArtista(repertorioBandaDTO.getMusica().getArtista());
		musicaRepertorio.getMusica().setTonalidade(Tonalidade.encontrarPeloNumero(repertorioBandaDTO.getMusica().getTonalidade()));
		musicaRepertorio.getMusica().setUrlSpotify(repertorioBandaDTO.getMusica().getUrlSpotify());
		musicaRepertorio.getMusica().setUrlYoutube(repertorioBandaDTO.getMusica().getUrlYoutube());
		musicaRepertorio.getMusica().setDescricao(repertorioBandaDTO.getMusica().getDescricao());
		musicaRepertorio.getMusica().setObservacao(repertorioBandaDTO.getMusica().getObservacao());
		musicaRepertorio.getMusica().setDuracao(repertorioBandaDTO.getMusica().getDuracao());
		musicaRepertorio.getMusica().setLetra(repertorioBandaDTO.getMusica().getLetra());
		musicaRepertorio.getMusica().setBpm(repertorioBandaDTO.getMusica().getBpm());
		musicaRepertorio.setIndice(repertorioBandaDTO.getIndice());
		musicaRepertorio.setPosicaoShow(repertorioBandaDTO.getPosicaoShow());
		musicaRepertorio.setEnergia(repertorioBandaDTO.getEnergia());
		musicaRepertorio.setRelevancia(repertorioBandaDTO.getRelevancia());
		repertorioBandaService.salvar(musicaRepertorio);

		if (musicaRepertorio.getBanda() != null) {
			audioMusicaService.renomear(musicaRepertorio.getBanda().getId(),
					tituloAnterior, artistaAnterior,
					musicaRepertorio.getMusica().getTitulo(),
					musicaRepertorio.getMusica().getArtista());
		}

		return montarPropagacao(musicaRepertorio, anterior, tituloAnterior, artistaAnterior);
	}

	/**
	 * Monta a pergunta "quer refletir isso nos shows pendentes?" -- ou devolve {@code null}
	 * quando nao ha o que perguntar.
	 *
	 * <p>Sao tres portoes, e todos existem para a tela nao interromper quem so queria salvar:
	 * a edicao precisa ter mexido em algum campo que tambem vive na linha do evento (arrastar a
	 * musica de posicao ou mudar a energia nao conta), a musica precisa estar no setlist de pelo
	 * menos um show pendente, e a busca e feita pela chave anterior, que e o unico jeito de
	 * reencontrar a musica no show quando o que mudou foi justamente o nome dela.
	 */
	private PropagacaoMusicaRepertorioDTO montarPropagacao(RepertorioBanda repertorio, Musica anterior,
			String tituloAnterior, String artistaAnterior) {
		if (repertorio.getBanda() == null) {
			return null;
		}

		List<CampoMusica> campos = CampoMusica.alterados(anterior, repertorio.getMusica());
		if (campos.isEmpty()) {
			return null;
		}

		Long idBanda = repertorio.getBanda().getId();
		List<Show> shows = showsPendentesComMusica(idBanda, ChaveMusica.de(tituloAnterior, artistaAnterior));
		if (shows.isEmpty()) {
			return null;
		}

		PropagacaoMusicaRepertorioDTO propagacao = new PropagacaoMusicaRepertorioDTO();
		propagacao.setId(repertorio.getId());
		propagacao.setIdBanda(idBanda);
		propagacao.setTituloAnterior(tituloAnterior);
		propagacao.setArtistaAnterior(artistaAnterior);
		propagacao.setCampos(campos);
		propagacao.setShows(shows.stream().map(EventoPendenteConviteDTO::new).toList());
		return propagacao;
	}

	@Override
	@Transactional
	public int propagarMusicaParaShowsPendentes(PropagacaoMusicaRepertorioDTO propagacao) {
		RepertorioBanda repertorio = repertorioBandaService.buscarPorId(propagacao.getId());
		if (repertorio.getBanda() == null || !repertorio.getBanda().getId().equals(propagacao.getIdBanda())) {
			throw new InvalidParamException("Música não pertence ao repertório da banda informada");
		}
		// Estoura para quem nao e da banda: o payload volta do navegador e aponta que linha ler.
		musicoBandaService.buscarPorIdUsuarioEIdBanda(Context.getUsuarioLogado().getId(), propagacao.getIdBanda());

		List<CampoMusica> campos = propagacao.getCampos() == null ? List.of()
				: propagacao.getCampos().stream().filter(Objects::nonNull).toList();
		if (campos.isEmpty()) {
			return 0;
		}

		// Os valores saem sempre da linha da banda, ja salva -- o payload so escolhe quais campos
		// copiar. E por isso que nao ha o que validar no conteudo que voltou do navegador.
		List<RepertorioEvento> itens = itensDosShowsPendentes(propagacao.getIdBanda(),
				ChaveMusica.de(propagacao.getTituloAnterior(), propagacao.getArtistaAnterior()));

		itens.forEach(item -> {
			campos.forEach(campo -> campo.copiar(repertorio.getMusica(), item.getMusica()));
			repertorioEventoService.salvar(item);
		});

		long showsAtualizados = itens.stream().map(item -> item.getId().getIdEvento()).distinct().count();
		log.info("Musica {} do repertorio da banda {} propagada para {} show(s) pendente(s): {}",
				propagacao.getId(), propagacao.getIdBanda(), showsAtualizados, campos);
		return (int) showsAtualizados;
	}

	private List<Show> showsPendentesComMusica(Long idBanda, String chaveMusica) {
		List<Show> pendentes = showDao.buscarFuturosNaoRealizadosPorBanda(idBanda);
		Set<Long> comAMusica = itensComMusica(idBanda, pendentes, chaveMusica).stream()
				.map(item -> item.getId().getIdEvento())
				.collect(Collectors.toSet());

		return pendentes.stream().filter(show -> comAMusica.contains(show.getId())).toList();
	}

	private List<RepertorioEvento> itensDosShowsPendentes(Long idBanda, String chaveMusica) {
		return itensComMusica(idBanda, showDao.buscarFuturosNaoRealizadosPorBanda(idBanda), chaveMusica);
	}

	/**
	 * As linhas de repertorio desses eventos que sao a musica procurada.
	 *
	 * <p>Momentos ("dar boa noite") ficam de fora mesmo tendo titulo: um momento sem artista
	 * produziria a chave {@code "titulo|"} e poderia colidir com uma musica cadastrada sem
	 * artista, e ai o texto do roteiro seria sobrescrito pela letra de uma cancao.
	 */
	private List<RepertorioEvento> itensComMusica(Long idBanda, List<Show> shows, String chaveMusica) {
		List<Long> idsShows = shows.stream().map(Show::getId).toList();

		return repertorioEventoService.buscarPorEventos(idBanda, TipoEvento.SHOW, idsShows).stream()
				.filter(item -> !item.isMomento())
				.filter(item -> ChaveMusica.de(item.getMusica().getTitulo(), item.getMusica().getArtista())
						.equals(chaveMusica))
				.toList();
	}

    @Override
    @Transactional
    public void alterarPermissaoMembro(EditarMembroMusicoBandaDTO permissaoMusicoDTO) {
		MusicoBanda usuario = musicoBandaService.buscarPorIdUsuarioEIdBanda(Context.getUsuarioLogado().getId(), permissaoMusicoDTO.getIdBanda());
		if(!usuario.getPermissoes().contains(PermissaoMusico.ADMINISTRADOR) || !usuario.getPermissoes().contains(PermissaoMusico.FUNDADOR)){
			throw new InternalException("Usuário não tem permissão para fazer essa alteração.");
		}

        MusicoBanda musicoBanda = musicoBandaService.buscarPorIdUsuarioEIdBanda(permissaoMusicoDTO.getIdUsuario(), permissaoMusicoDTO.getIdBanda());
		musicoBanda.setInstrumentos(permissaoMusicoDTO.getInstrumentos());
		musicoBanda.setPermissoes(permissaoMusicoDTO.getPermissoes());
		musicoBandaService.salvar(musicoBanda);
    }

    private void setarInformacoesEditadas(Banda banda, EdicaoBandaDTO bandaDTO, String urlLogo, String urlBanner) {
		banda.setNome(bandaDTO.getNome());
		banda.setDescricao(bandaDTO.getDescricao());
		banda.setCategoria(bandaDTO.getCategoria());
		if (bandaDTO.getNacionalidade() != null && !bandaDTO.getNacionalidade().isBlank()) {
			banda.setNacionalidade(normalizarNacionalidade(bandaDTO.getNacionalidade()));
		} else if (banda.getNacionalidade() == null) {
			banda.setNacionalidade("BR");
		}
		banda.setAnoFundacao(validarAnoFundacao(bandaDTO.getAnoFundacao()));
		banda.setTipoRepertorio(converterTipoRepertorio(bandaDTO.getTipoRepertorio()));
		banda.setUrlLogo(urlLogo);
		banda.setUrlBanner(urlBanner);
		banda.setInstagramUrl(bandaDTO.getInstagramUrl());
		banda.setFacebookUrl(bandaDTO.getFacebookUrl());
		banda.setYoutubeUrl(bandaDTO.getYoutubeUrl());
		banda.getParametros().setPermiteEntradaPorConvite(bandaDTO.getPermiteEntradaPorConvite());
		banda.getParametros().setExigirAprovacaoCompromissos(bandaDTO.getExigirAprovacaoCompromissos());
	}

	/**
	 * Ano de fundacao e opcional; quando vem preenchido tem que ser plausivel, senao o perfil
	 * mostraria "desde 12" ou um ano no futuro.
	 */
	private Integer validarAnoFundacao(Integer anoFundacao) {
		if (anoFundacao == null) {
			return null;
		}
		int anoAtual = LocalDate.now().getYear();
		if (anoFundacao < ANO_FUNDACAO_MINIMO || anoFundacao > anoAtual) {
			throw new InvalidParamException(
					"Ano de fundação deve estar entre " + ANO_FUNDACAO_MINIMO + " e " + anoAtual);
		}
		return anoFundacao;
	}

	/** Tipo de repertorio e opcional; string vazia equivale a "nao informado". */
	private TipoRepertorio converterTipoRepertorio(String tipoRepertorio) {
		if (tipoRepertorio == null || tipoRepertorio.isBlank()) {
			return null;
		}
		try {
			return TipoRepertorio.valueOf(tipoRepertorio.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new InvalidParamException("Tipo de repertório inválido: " + tipoRepertorio);
		}
	}

	/** Nacionalidade e um ISO 3166-1 alpha-2 maiusculo; sem valor, assume Brasil. */
	private String normalizarNacionalidade(String nacionalidade) {
		if (nacionalidade == null || nacionalidade.isBlank()) {
			return "BR";
		}
		return nacionalidade.trim().toUpperCase();
	}

	private void cadastrarUsuarioEmBanda(CadastroBandaDTO bandaDTO, Banda novaBanda, List<PermissaoMusico> permissoes) {
		musicoBandaService.cadastrarUsuarioEmBanda(Context.getUsuarioLogado(), novaBanda, bandaDTO.getInstrumento(),
				permissoes, bandaDTO.getCorHex());
	}

	@Override
	public ResponseEntity<?> buscarBandaParaIngressar(Long idBanda) {
		Banda banda = buscarPorId(idBanda);
		if (banda == null) {
			return ResponseEntity.status(404).body("Banda não existe");
		}
		try {
			verificarSeUsuarioJaEstaNaBanda(banda);
			verificarSeBandaPermiteEntradaPorConvite(banda);
		} catch (ConflictException c) {
			return ResponseEntity.status(409).body(c.getMessage());
		} catch (RestrictionException r) {
			return ResponseEntity.status(403).body(r.getMessage());
		}
		return ResponseEntity.ok(new BandaDTO(banda));
	}

	@Override
	public Integer buscarQuantidadeDeShows(Long idBanda) {
		Banda banda = buscarPorId(idBanda);
		return banda == null ? 0 : banda.getShows().stream().filter(show -> show.getStatus().equals(StatusEvento.PENDENTE)).toList().size();
	}

	@Override
	public Integer buscarQuantidadeDeEnsaios(Long idBanda) {
		Banda banda = buscarPorId(idBanda);
		return banda == null ? 0 : banda.getEnsaios().stream().filter(ensaio -> ensaio.getStatus().equals(StatusEvento.PENDENTE)).toList().size();
	}

	@Override
	public Integer buscarQuantidadeDeNotificacoes(Long idBanda) {
		return 0;
	}

	@Override
	public Integer buscarQuantidadeDeMembros(Long idBanda) {
		Banda banda = buscarPorId(idBanda);
		return banda == null ? 0 : banda.getMusicos().size() + banda.getMembrosFantasma().size();
	}

	@Override
	public Integer buscarQuantidadeDeMusicasNoRepertorio(Long idBanda) {
		return bandaDao.buscarQuantidadeDeMusicasNoRepertorio(idBanda);
	}

	@Override
	public void sairDaBanda(Long idBanda, Long idUsuario) {
		Integer quantidadeMembros = buscarQuantidadeDeMembros(idBanda);
		if (quantidadeMembros <= 1) {
			deletarBanda(idBanda);
		} else {
			musicoBandaService.expulsar(idBanda, idUsuario);
		}
	}

	@Override
	public ShowsFuturosDTO buscarShowsFuturosBanda(Long idBanda, Long idUsuario) {
		ShowsFuturosDTO showsFuturosDTO = new ShowsFuturosDTO();

		CompletableFuture<List<ShowDTO>> showsPendentesFuture = buscarShowsPendentesAsync(idBanda, idUsuario);
		CompletableFuture<List<ShowDTO>> showsAguardandoFuture = buscarShowsAguardandoAsync(idBanda, idUsuario);

		CompletableFuture.allOf(showsPendentesFuture, showsAguardandoFuture).join();

		try {
			showsFuturosDTO.setShowsPendentes(showsPendentesFuture.get());
			showsFuturosDTO.setShowsAguardando(showsAguardandoFuture.get());
		} catch (Exception e) {
			throw new InternalException("Erro ao buscar shows!");
		}

		return showsFuturosDTO;
	}

	@Override
	public EnsaiosFuturosDTO buscarEnsaiosFuturosBanda(Long idBanda, Long idUsuario) {
		EnsaiosFuturosDTO ensaiosFuturosDTO = new EnsaiosFuturosDTO();

		CompletableFuture<List<EnsaioDTO>> ensaiosPendentesFuture = buscarEnsaiosPendentesAsync(idBanda, idUsuario);
		CompletableFuture<List<EnsaioDTO>> ensaiosAguardandoFuture = buscarEnsaiosAguardandoAsync(idBanda, idUsuario);

		CompletableFuture.allOf(ensaiosPendentesFuture, ensaiosAguardandoFuture).join();

		try {
			ensaiosFuturosDTO.setEnsaiosPendentes(ensaiosPendentesFuture.get());
			ensaiosFuturosDTO.setEnsaiosAguardando(ensaiosAguardandoFuture.get());
		} catch (Exception e) {
			throw new InternalException("Erro ao buscar ensaios");
		}

		return ensaiosFuturosDTO;
	}

	@Override
	public List<RepertorioBandaDTO> buscarRepertorio(Long idBanda) {
		//ordernar por índice
		return buscarPorId(idBanda).getRepertorio().stream().map(RepertorioBandaDTO::new)
				.sorted(Comparator.comparing(RepertorioBandaDTO::getIndice, Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList());
	}

	@Override
	public void adicionarMusicaAoRepertorio(RepertorioBandaDTO repertorioBandaDTO) {
		repertorioBandaDTO.setIndice(repertorioBandaService.buscarUltimoIndice(repertorioBandaDTO.getIdBanda()) + 1);
		RepertorioBanda repertorioBanda = criarRepertorioBanda(repertorioBandaDTO);
		salvarRepertorio(repertorioBanda);
	}

	private RepertorioBanda criarRepertorioBanda(RepertorioBandaDTO repertorioBandaDTO) {
		RepertorioBanda repertorioBanda = new RepertorioBanda();
		repertorioBanda.setBanda(buscarPorId(repertorioBandaDTO.getIdBanda()));

		Musica musica = new Musica();
		BeanUtils.copyProperties(repertorioBandaDTO.getMusica(), musica);
		musica.setTonalidade(Tonalidade.encontrarPeloNumero(repertorioBandaDTO.getMusica().getTonalidade()));

		repertorioBanda.setMusica(musica);
		repertorioBanda.setIndice(repertorioBandaDTO.getIndice());
		repertorioBanda.setPosicaoShow(repertorioBandaDTO.getPosicaoShow());
		repertorioBanda.setEnergia(repertorioBandaDTO.getEnergia());
		repertorioBanda.setRelevancia(repertorioBandaDTO.getRelevancia());
		
		return repertorioBanda;
	}

	private void salvarRepertorio(RepertorioBanda repertorioBanda) {
		repertorioBandaService.salvar(repertorioBanda);
	}

	private CompletableFuture<List<ShowDTO>> buscarShowsAguardandoAsync(Long idBanda, Long idUsuario) {
		return CompletableFuture.supplyAsync(() -> buscarShowsAguardandoPorBandaEUsuario(idBanda, idUsuario));
	}

	private CompletableFuture<List<ShowDTO>> buscarShowsPendentesAsync(Long idBanda, Long idUsuario) {
		return CompletableFuture.supplyAsync(() -> buscarShowsPendentesPorBandaEUsuario(idBanda, idUsuario));
	}

	private CompletableFuture<List<EnsaioDTO>> buscarEnsaiosAguardandoAsync(Long idBanda, Long idUsuario) {
		return CompletableFuture.supplyAsync(() -> buscarEnsaiosAguardandoPorBandaEUsuario(idBanda, idUsuario));
	}

	private CompletableFuture<List<EnsaioDTO>> buscarEnsaiosPendentesAsync(Long idBanda, Long idUsuario) {
		return CompletableFuture.supplyAsync(() -> buscarEnsaiosPendentesPorBandaEUsuario(idBanda, idUsuario));
	}

	private List<EnsaioDTO> buscarEnsaiosAguardandoPorBandaEUsuario(Long idBanda, Long idUsuario) {
		return eventoHelperService.buscarEnsaiosAguardandoPorBandaEUsuarioOrdenadoPorData(idBanda, idUsuario).stream()
				.map(EnsaioDTO::new).collect(Collectors.toList());
	}

	private List<EnsaioDTO> buscarEnsaiosPendentesPorBandaEUsuario(Long idBanda, Long idUsuario) {
		return eventoHelperService.buscarEnsaiosPendentesPorBandaEUsuarioOrdenadoPorData(idBanda, idUsuario).stream()
				.map(EnsaioDTO::new).collect(Collectors.toList());
	}

	private List<ShowDTO> buscarShowsAguardandoPorBandaEUsuario(Long idBanda, Long idUsuario) {
		return eventoHelperService.buscarShowsAguardandoPorBandaEUsuarioOrdenadoPorData(idBanda, idUsuario).stream()
				.map(ShowDTO::new).collect(Collectors.toList());
	}

	private List<ShowDTO> buscarShowsPendentesPorBandaEUsuario(Long idBanda, Long idUsuario) {
		return eventoHelperService.buscarShowsPendentesPorBandaEUsuarioOrdenadoPorData(idBanda, idUsuario).stream()
				.map(ShowDTO::new).collect(Collectors.toList());
	}

	private void deletarBanda(Long idBanda) {
		Banda banda = buscarPorId(idBanda);
		for (MusicoBanda musico : banda.getMusicos()) {
			musicoBandaService.expulsar(idBanda, musico.getId().getIdUsuario());
		}
		bandaDao.delete(banda);
	}

	public Banda buscarPorId(Long idBanda) {
		return bandaDao.findById(idBanda).orElseThrow();
	}

	private static void verificarSeBandaPermiteEntradaPorConvite(Banda banda) {
		if (!banda.getParametros().getPermiteEntradaPorConvite()) {
			throw new RestrictionException("A banda " + banda.getNome() + " não permite entrada por convite");
		}
	}

	private static void verificarSeUsuarioJaEstaNaBanda(Banda banda) {
		for (MusicoBanda musicoBanda : banda.getMusicos()) {
			if (musicoBanda.getId().getIdUsuario().equals(Context.getUsuarioLogado().getId()))
				throw new ConflictException("Você já está na banda " + banda.getNome());

		}
	}

	private Banda atualizaBandaFromCadastroDTO(Banda banda, CadastroBandaDTO bandaDTO, String urlLogo, String urlBanner) {
		banda.setCategoria(bandaDTO.getCategoria());
		banda.setNome(bandaDTO.getNome());
		banda.setDescricao(bandaDTO.getDescricao());
		banda.setNacionalidade(normalizarNacionalidade(bandaDTO.getNacionalidade()));
		banda.setAnoFundacao(validarAnoFundacao(bandaDTO.getAnoFundacao()));
		banda.setTipoRepertorio(converterTipoRepertorio(bandaDTO.getTipoRepertorio()));
		banda.setUrlLogo(urlLogo);
		banda.setUrlBanner(urlBanner);
		banda.setInstagramUrl(bandaDTO.getInstagramUrl());
		banda.setFacebookUrl(bandaDTO.getFacebookUrl());
		banda.setYoutubeUrl(bandaDTO.getYoutubeUrl());
		banda.setDataInclusao(LocalDate.now());

		ParametrosBanda parametros = new ParametrosBanda();
		parametros.setExigirAprovacaoCompromissos(Boolean.TRUE.equals(bandaDTO.getExigirAprovacaoCompromissos()));
		parametros.setPermiteEntradaPorConvite(bandaDTO.getPermiteEntradaPorConvite() == null || bandaDTO.getPermiteEntradaPorConvite());
		parametros.setListarObservacaoRepertorio(false);
		banda.setParametros(parametros);

		return banda;
	}

	@Override
	public List<Banda> buscarSugestoes(BuscaBandaDTO dto) {
		List<Banda> resultados = bandaDao.buscarSugestoes(dto);

		LevenshteinDistance levenshtein = new LevenshteinDistance();

		return resultados.stream()
				.sorted(Comparator.comparingInt(banda -> 
					dto.getNome() != null && !dto.getNome().isEmpty() 
						? levenshtein.apply(dto.getNome().toLowerCase(), banda.getNome().toLowerCase())
						: 0))
				.limit(20)
				.collect(Collectors.toList());
	}

	@Override
	public void removerMusicaDoRepertorio(Long id, Long idBanda) {
		RepertorioBanda repertorioBanda = repertorioBandaService.buscarPorId(id);
		repertorioBandaService.deletar(repertorioBanda);
	}

	@Override
	@Transactional
	public void atualizarOrdemRepertorio(Long idBanda, List<RepertorioBandaDTO> repertorio) {
		repertorio.forEach(repertorioBandaDTO ->
			repertorioBandaService.updateIndice(repertorioBandaDTO.getId(), repertorioBandaDTO.getIndice())
		);
	}

}
