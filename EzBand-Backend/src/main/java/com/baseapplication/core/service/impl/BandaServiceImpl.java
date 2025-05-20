package com.baseapplication.core.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.model.embedded.ParametrosBanda;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dao.BandaDao;
import com.baseapplication.core.enums.Tonalidade;
import com.baseapplication.core.exception.ConflictException;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.exception.RestrictionException;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.RepertorioBanda;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BandaServiceImpl implements BandaService {

	@Autowired
	private BandaDao bandaDao;

	@Autowired
	private MusicoBandaService musicoBandaService;

	@Autowired
	private ImagemService imagemService;

	@Autowired
	private EventoHelperService eventoHelperService;

	@Autowired
	private RepertorioBandaService repertorioBandaService;

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
		musicoBandaService.cadastrarUsuarioEmBanda(
				Context.getUsuarioLogado(),
				bandaDao.findById(idBanda).get(),
				instrumentos,
				PermissaoMusico.MEMBRO_REGULAR
		);
	}

	@Override
	public void expulsarUsuario(Long idBanda, Long idUsuario) {
		musicoBandaService.expulsar(idBanda, idUsuario);
	}

	@Override
	public List<InfoMembroBandaDTO> buscarMembros(Long idBanda) {
		return musicoBandaService.buscarMembrosPorIdBanda(idBanda).stream().map(InfoMembroBandaDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	public void novaBanda(String bandaJson, MultipartFile logo) {
		CadastroBandaDTO bandaDTO;
		try {
			bandaDTO = new ObjectMapper().readValue(bandaJson, CadastroBandaDTO.class);
		} catch (JsonProcessingException e) {
			throw new InternalException("Erro ao converter json");
		}

		String urlLogo = imagemService.saveImageAndGetUrl(logo);
		Banda banda = novaBandaFromCadastroDTO(bandaDTO, urlLogo);
		cadastrarUsuarioEmBanda(bandaDTO, banda, PermissaoMusico.FUNDADOR);
	}

	@Override
	public void editarBanda(String bandaJson, MultipartFile logo) {
		EdicaoBandaDTO bandaDTO;
		try {
			bandaDTO = new ObjectMapper().readValue(bandaJson, EdicaoBandaDTO.class);
		} catch (JsonProcessingException e) {
			throw new InternalException("Erro ao converter json");
		}

		Banda banda = buscarPorId(bandaDTO.getId());

		String urlLogo = banda.getUrlLogo();
		if(logo != null){
			urlLogo = imagemService.saveImageAndGetUrl(logo);
		}

		setarInformacoesEditadas(banda, bandaDTO, urlLogo);
		bandaDao.save(banda);

	}

	private void setarInformacoesEditadas(Banda banda, EdicaoBandaDTO bandaDTO, String urlLogo) {
		banda.setNome(bandaDTO.getNome());
		banda.setDescricao(bandaDTO.getDescricao());
		banda.setCategoria(bandaDTO.getCategoria());
		banda.setUrlLogo(urlLogo);
		banda.getParametros().setPermiteEntradaPorConvite(bandaDTO.getPermiteEntradaPorConvite());
		banda.getParametros().setExigirAprovacaoCompromissos(bandaDTO.getExigirAprovacaoCompromissos());
	}

	private void cadastrarUsuarioEmBanda(CadastroBandaDTO bandaDTO, Banda novaBanda, PermissaoMusico permissaoMusico) {
		musicoBandaService.cadastrarUsuarioEmBanda(Context.getUsuarioLogado(), novaBanda, bandaDTO.getInstrumento(), permissaoMusico);
	}

	@Override
	public ResponseEntity<?> buscarBandaParaIngressar(Long idBanda) {
		Banda banda = buscarPorId(idBanda);
		if(banda == null){
			return ResponseEntity.status(404).body("Banda não existe");
		}
		try{
			verificarSeUsuarioJaEstaNaBanda(banda);
			verificarSeBandaPermiteEntradaPorConvite(banda);
		}catch (ConflictException c ){
			return ResponseEntity.status(409).body(c.getMessage());
		}catch (RestrictionException r ){
			return ResponseEntity.status(403).body(r.getMessage());
		}
		return ResponseEntity.ok(new BandaDTO(banda));
	}

	@Override
	public Integer buscarQuantidadeDeShows(Long idBanda) {
		return bandaDao.buscarQuantidadeDeShows(idBanda, Context.getUsuarioLogado().getId());
	}

	@Override
	public Integer buscarQuantidadeDeEnsaios(Long idBanda) {
		return bandaDao.buscarQuantidadeDeEnsaios(idBanda, Context.getUsuarioLogado().getId());
	}

	@Override
	public Integer buscarQuantidadeDeNotificacoes(Long idBanda) {
		return bandaDao.buscarQuantidadeDeNotificacoes(idBanda);
	}

	@Override
	public Integer buscarQuantidadeDeMembros(Long idBanda) {
		return bandaDao.buscarQuantidadeDeMembros(idBanda);
	}

	@Override
	public Integer buscarQuantidadeDeMusicasNoRepertorio(Long idBanda) {
		return bandaDao.buscarQuantidadeDeMusicasNoRepertorio(idBanda);
	}

	@Override
	public Integer getNivelPermissaoUsuario(Long idBanda, Long idUsuario) {
		return musicoBandaService.buscarPorIdUsuarioEIdBanda(idUsuario, idBanda).getPermissao().getNivel();
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
	public List<MusicaDTO> buscarRepertorio(Long idBanda) {
		return buscarPorId(idBanda).getRepertorio().stream().map(i -> new MusicaDTO(i.getMusica()))
				.collect(Collectors.toList());
	}

	@Override
	public void adicionarMusicaAoRepertorio(RepertorioBandaDTO repertorioBandaDTO) {
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
		return bandaDao.findById(idBanda).orElse(null);
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

	private Banda novaBandaFromCadastroDTO(CadastroBandaDTO bandaDTO, String urlLogo) {
		Banda novaBanda = new Banda();
		novaBanda.setCategoria(bandaDTO.getCategoria());
		novaBanda.setNome(bandaDTO.getNome());
		novaBanda.setDescricao(bandaDTO.getDescricao());
		novaBanda.setUrlLogo(urlLogo);
		novaBanda.setDataInclusao(LocalDate.now());

		ParametrosBanda parametros = new ParametrosBanda();
		parametros.setExigirAprovacaoCompromissos(false);
		parametros.setPermiteEntradaPorConvite(true);
		parametros.setListarObservacaoRepertorio(false);
		novaBanda.setParametros(parametros);

		novaBanda = bandaDao.save(novaBanda);
		return novaBanda;
	}

}
