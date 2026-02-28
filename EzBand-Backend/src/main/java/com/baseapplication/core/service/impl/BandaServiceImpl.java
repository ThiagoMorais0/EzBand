package com.baseapplication.core.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.baseapplication.core.dto.BuscaBandaDTO;
import com.baseapplication.core.dto.EditarMembroMusicoBandaDTO;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.event.events.ConviteParaUsuarioIngressarBandaEvent;
import com.baseapplication.core.event.events.UsuarioExpulsoDeBandaEvent;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.embedded.ParametrosBanda;
import com.baseapplication.core.service.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dao.BandaDao;
import com.baseapplication.core.dto.CadastroBandaDTO;
import com.baseapplication.core.dto.EdicaoBandaDTO;
import com.baseapplication.core.dto.EnsaiosFuturosDTO;
import com.baseapplication.core.dto.InfoMembroBandaDTO;
import com.baseapplication.core.dto.RepertorioBandaDTO;
import com.baseapplication.core.dto.ShowsFuturosDTO;
import com.baseapplication.core.enums.Tonalidade;
import com.baseapplication.core.exception.ConflictException;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.exception.RestrictionException;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.embedded.Musica;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.EventoHelperService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.MusicoBandaService;
import com.baseapplication.core.service.RepertorioBandaService;
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
	private final NotificacaoService notificacaoService;
	private final MembroFantasmaService membroFantasmaService;

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
				bandaDao.findById(idBanda).orElseThrow(() -> new ResourceNotFoundException("Banda não encontrada")));
	}

	@Override
	public void cadastrarUsuario(Long idBanda, Long idUsuario, String instrumentos) {
		musicoBandaService.cadastrarUsuarioEmBanda(Context.getUsuarioLogado(), bandaDao.findById(idBanda).get(),
				instrumentos, List.of(PermissaoMusico.MEMBRO_REGULAR));
	}

	@Override
	public void expulsarUsuario(Long idBanda, Long idUsuario) {
		musicoBandaService.expulsar(idBanda, idUsuario);
		notificacaoService.enviarNotificacao(new UsuarioExpulsoDeBandaEvent(idUsuario, idBanda));
	}

	@Override
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
	public void novaBanda(String bandaJson, MultipartFile logo) {
		log.info("Nova banda: " + bandaJson);
		CadastroBandaDTO bandaDTO;
		try {
			bandaDTO = new ObjectMapper().readValue(bandaJson, CadastroBandaDTO.class);
		} catch (JsonProcessingException e) {
			throw new InternalException("Erro ao converter json");
		}

		Banda banda = bandaDao.save(new Banda());
		String urlLogo;
		if(logo != null){
			urlLogo = imagemService.saveImageAndGetUrl(logo, "bandlogos",
					banda.getId() + "." + FileUtils.getSufix(logo));
		}else{
			urlLogo = "default";
		}

		banda = atualizaBandaFromCadastroDTO(banda, bandaDTO, urlLogo);
		bandaDao.save(banda);
		cadastrarUsuarioEmBanda(bandaDTO, banda, PermissaoMusico.getAll());
	}

	@Override
	public void editarBanda(String bandaJson, MultipartFile logo, Boolean removerLogo) {
		EdicaoBandaDTO bandaDTO;
		try {
			bandaDTO = new ObjectMapper().readValue(bandaJson, EdicaoBandaDTO.class);
		} catch (JsonProcessingException e) {
			throw new InternalException("Erro ao converter json");
		}

		Banda banda = buscarPorId(bandaDTO.getId());

		String urlLogo = banda.getUrlLogo();
		if (logo != null) {
			urlLogo = imagemService.saveImageAndGetUrl(logo, "bandlogos",
					banda.getId() + "." + FileUtils.getSufix(logo));
		} else if (removerLogo) {
			urlLogo = "default";
		}

		setarInformacoesEditadas(banda, bandaDTO, urlLogo);
		bandaDao.save(banda);

	}

	@Override
	public List<String> getPermissoesMusico(Long idBanda, Long idUsuario) {
		MusicoBanda musicoBanda = musicoBandaService.buscarPorIdUsuarioEIdBanda(idUsuario, idBanda);
		if (musicoBanda == null) {
			throw new InternalException("Músico não encontrado");
		}
		return musicoBanda.getPermissoes().stream().map(Enum::toString).toList();
	}

	@Override
	public void enviarConviteParaUsuarioIngressarBanda(Long idBanda, Long idUsuarioConvidado) {
		notificacaoService.enviarNotificacao(new ConviteParaUsuarioIngressarBandaEvent(idUsuarioConvidado, buscarPorId(idBanda)));
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
	public void atualizarMusicaRertorio(RepertorioBandaDTO repertorioBandaDTO) {
		RepertorioBanda musicaRepertorio = repertorioBandaService.buscarPorId(repertorioBandaDTO.getId());
		musicaRepertorio.getMusica().setTitulo(repertorioBandaDTO.getMusica().getTitulo());
		musicaRepertorio.getMusica().setArtista(repertorioBandaDTO.getMusica().getArtista());
		musicaRepertorio.getMusica().setTonalidade(Tonalidade.encontrarPeloNumero(repertorioBandaDTO.getMusica().getTonalidade()));
		musicaRepertorio.getMusica().setUrlSpotify(repertorioBandaDTO.getMusica().getUrlSpotify());
		musicaRepertorio.getMusica().setUrlYoutube(repertorioBandaDTO.getMusica().getUrlYoutube());
		musicaRepertorio.getMusica().setDescricao(repertorioBandaDTO.getMusica().getDescricao());
		musicaRepertorio.getMusica().setObservacao(repertorioBandaDTO.getMusica().getObservacao());
		musicaRepertorio.setIndice(repertorioBandaDTO.getIndice());
		musicaRepertorio.setPosicaoShow(repertorioBandaDTO.getPosicaoShow());
		musicaRepertorio.setEnergia(repertorioBandaDTO.getEnergia());
		musicaRepertorio.setRelevancia(repertorioBandaDTO.getRelevancia());
		repertorioBandaService.salvar(musicaRepertorio);
	}

    @Override
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

    private void setarInformacoesEditadas(Banda banda, EdicaoBandaDTO bandaDTO, String urlLogo) {
		banda.setNome(bandaDTO.getNome());
		banda.setDescricao(bandaDTO.getDescricao());
		banda.setCategoria(bandaDTO.getCategoria());
		banda.setUrlLogo(urlLogo);
		banda.getParametros().setPermiteEntradaPorConvite(bandaDTO.getPermiteEntradaPorConvite());
		banda.getParametros().setExigirAprovacaoCompromissos(bandaDTO.getExigirAprovacaoCompromissos());
	}

	private void cadastrarUsuarioEmBanda(CadastroBandaDTO bandaDTO, Banda novaBanda, List<PermissaoMusico> permissoes) {
		musicoBandaService.cadastrarUsuarioEmBanda(Context.getUsuarioLogado(), novaBanda, bandaDTO.getInstrumento(),
				permissoes);
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

	private Banda atualizaBandaFromCadastroDTO(Banda banda, CadastroBandaDTO bandaDTO, String urlLogo) {
		banda.setCategoria(bandaDTO.getCategoria());
		banda.setNome(bandaDTO.getNome());
		banda.setDescricao(bandaDTO.getDescricao());
		banda.setUrlLogo(urlLogo);
		banda.setDataInclusao(LocalDate.now());

		ParametrosBanda parametros = new ParametrosBanda();
		parametros.setExigirAprovacaoCompromissos(false);
		parametros.setPermiteEntradaPorConvite(true);
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
	public void atualizarOrdemRepertorio(Long idBanda, List<RepertorioBandaDTO> repertorio) {
		repertorio.forEach(repertorioBandaDTO -> {
			RepertorioBanda repertorioBanda = repertorioBandaService.buscarPorId(repertorioBandaDTO.getId());
			repertorioBanda.setIndice(repertorioBandaDTO.getIndice());
			repertorioBandaService.salvar(repertorioBanda);
		});
	}

}
