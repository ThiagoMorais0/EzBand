package com.baseapplication.core.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.event.events.NovoSeguidorEvent;
import com.baseapplication.core.event.events.SolicitacaoIngressarBandaEvent;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.InstrumentoUsuario;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dao.InstrumentoUsuarioDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoRelacionamento;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.InvalidParamException;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.EmailService;
import com.baseapplication.core.service.EventoHelperService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.NotificacaoService;
import com.baseapplication.core.service.RelacionamentoSeguidorService;
import com.baseapplication.core.service.ReporteDeErroService;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {
	private final UsuarioDao usuarioDao;
	private final InstrumentoUsuarioDao instrumentoUsuarioDao;
	private final BandaService bandaService;
	private final NotificacaoService notificacaoService;
	private final EventoHelperService eventoHelperService;
	private final ImagemService imagemService;
	private final ReporteDeErroService reporteDeErroService;
	private final EmailService emailService;
	private final RelacionamentoSeguidorService relacionamentoSeguidorService;

//    @Override
//    public Usuario findByLogin(String login) {
//        return usuarioDao.findByUsername(login);
//    }

	@Override
	public Usuario findByEmail(String email) {
		return usuarioDao.findByEmail(email);
	}

	@Override
	public Usuario salvar(Usuario usuario) {
		return usuarioDao.save(usuario);
	}

	@Override
	public InfoUsuarioPainelDTO buscarInfoPainel() {

		Long idUsuario = Context.getUsuarioLogado().getId();
		CompletableFuture<List<BandaDTO>> bandasFuture = buscarBandasDoUsuarioAsync(idUsuario);
//		CompletableFuture<Integer> notificacoesFuture = buscarQuantidadeNotificacoesAsync(idUsuario);
		CompletableFuture<QuantidadeParticipacoesEspeciaisDTO> participacoesEspeciaisFuture = buscarQuantidadeParticipacoesEspeciaisAsync(
				idUsuario);
		CompletableFuture<Integer> proximosEventosFuture = buscarQuantidadeProximosEventosAsync(idUsuario);

		CompletableFuture.allOf(bandasFuture, participacoesEspeciaisFuture, proximosEventosFuture)
				.join();

		InfoUsuarioPainelDTO retorno = null;
		try {
			retorno = new InfoUsuarioPainelDTO(bandasFuture.get(), 0,
					participacoesEspeciaisFuture.get(), proximosEventosFuture.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			throw new InternalException("Erro interno ao buscar info painel");
		}
		return retorno;

	}

	private CompletableFuture<Integer> buscarQuantidadeProximosEventosAsync(Long idUsuario) {
		return CompletableFuture.supplyAsync(() -> buscarQuantidadeProximosEventos(idUsuario));
	}

	private CompletableFuture<QuantidadeParticipacoesEspeciaisDTO> buscarQuantidadeParticipacoesEspeciaisAsync(Long idUsuario) {
		return CompletableFuture.supplyAsync(() -> buscarQuantidadeParticipacoesEspeciais(idUsuario));
	}

	private QuantidadeParticipacoesEspeciaisDTO buscarQuantidadeParticipacoesEspeciais(Long idUsuario) {
		return usuarioDao.buscarQuantidadeParticipacoesEspeciais(idUsuario);
	}

//	private CompletableFuture<Integer> buscarQuantidadeNotificacoesAsync(Long idUsuario) {
////		return CompletableFuture.supplyAsync(() -> buscarQuantidadeNotificacoes(idUsuario));
//	}

	private Integer buscarQuantidadeProximosEventos(Long idUsuario) {
		return usuarioDao.buscarQuantidadeProximosEventos(idUsuario);
	}

	@Override
	public void atualizarInformacoesPerfil(InfoPerfilUsuarioDTO infoPerfil) {
		Usuario usuario = usuarioDao.findByEmail(infoPerfil.getEmail());
		if (usuario == null)
			throw new ResourceNotFoundException("Usuário não encontrado");
		if (usuario.getId() != infoPerfil.getId())
			throw new InvalidParamException("ID e e-mail divergentes");
		BeanUtils.copyProperties(infoPerfil, usuario);
		usuarioDao.save(usuario);
	}

	@Override
	public Usuario buscarPorId(Long id) {
		return usuarioDao.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o id " + id));
	}

	@Override
	public Usuario findByCelular(String celular) {
		return usuarioDao.findByCelular(celular);
	}

	@Override
	public Usuario buscarPorContato(String contato, TipoContato tipoContato) {
		switch (tipoContato) {
		case EMAIL -> {
			return findByEmail(contato);
		}
		case CELULAR -> {
			return findByCelular(contato);
		}
		default -> {
			throw new InvalidParamException("Tipo de contato inválido");
		}
		}
	}

	@Override
	public Boolean verificarEmailJaCadastrado(String email) {
		return usuarioDao.findByEmail(email) != null;
	}

	@Override
	@jakarta.transaction.Transactional
	public InfoPerfilUsuarioDTO editarUsuarioComImagem(String usuarioJson, MultipartFile imagem, Boolean removerImagemDePerfil) {
		InfoPerfilUsuarioDTO usuarioDTO = null;
		try {
			usuarioDTO = new ObjectMapper().readValue(usuarioJson, InfoPerfilUsuarioDTO.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new InternalException(e.getMessage());
		}
		Usuario usuario = buscarPorContato(usuarioDTO.getEmail(), TipoContato.EMAIL);
		String urlImagem = usuario.getUrlFotoPerfil();
		if (imagem != null) {
			String urlImagemAntiga = urlImagem;
			urlImagem = imagemService.saveImageAndGetUrl(imagem, "profilepictures",
					usuario.getId() + "_" + System.currentTimeMillis() + "." + FileUtils.getSufix(imagem));
			imagemService.deletarImagemPorUrl(urlImagemAntiga);
		} else if (removerImagemDePerfil) {
			imagemService.deletarImagemPorUrl(urlImagem);
			urlImagem = "default";
		}
		List<String> tiposUsuarioAtual = usuario.getTiposUsuario();
		BeanUtils.copyProperties(usuarioDTO, usuario);
		usuario.setTiposUsuario(tiposUsuarioAtual);
		usuario.setUrlFotoPerfil(urlImagem);
		usuario.setAtivo(true);
		usuarioDao.save(usuario);
		usuarioDTO.setUrlFotoPerfil(urlImagem);
		return usuarioDTO;
	}

	@Override
	public void enviarSolicitacaoParaIngressarBanda(Long idBanda, String instrumento) {
		Banda banda = bandaService.buscarPorId(idBanda);

		if(banda == null)
			throw new InternalException("Banda não encontrada");

		if(banda.getParametros() != null && !banda.getParametros().getPermiteEntradaPorConvite())
			throw new InternalException("Banda não permite ingresso por convite");

		notificacaoService.enviarNotificacao(new SolicitacaoIngressarBandaEvent(
				Context.getUsuarioLogado(), banda, instrumento
		));
	}

	/**
	 * Monta o perfil do usuario logado dentro de uma transacao e ja com as
	 * publicacoes carregadas por fetch join. O app roda com
	 * spring.jpa.open-in-view=false, entao montar o DTO fora daqui (por exemplo
	 * direto no controller) fecha a sessao antes do acesso as colecoes LAZY e
	 * dispara LazyInitializationException.
	 */
	@Override
	@Transactional(readOnly = true)
	public InfoPerfilUsuarioDTO buscarInformacoesDoPerfil() {
		Long idUsuario = Context.getUsuarioLogado().getId();
		Usuario usuario = usuarioDao.findByIdWithPublicacoes(idUsuario)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o id " + idUsuario));
		return new InfoPerfilUsuarioDTO(usuario);
	}

	/**
	 * Assim como buscarInformacoesDoPerfil, monta os DTOs dentro da transacao: as
	 * colecoes de Banda usadas pelo BandaDTO sao EAGER hoje, mas montar fora de
	 * sessao deixa o endpoint refem disso.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<BandaDTO> buscarBandasDoUsuario() {
		Long idUsuario = Context.getUsuarioLogado().getId();
		return Context.getUsuarioLogado().getBandas().stream().map(banda -> new BandaDTO(banda, idUsuario)).toList();
	}

	@Override
	@Transactional(readOnly = true)
	public InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(Long idUsuario) {
		Usuario usuario = usuarioDao.findByIdWithPublicacoes(idUsuario)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o id " + idUsuario));
		InfoPerfilUsuarioDTO dto = new InfoPerfilUsuarioDTO(usuario);
		dto.setTipoRelacionamento(relacionamentoSeguidorService.buscarTipoRelacionamento(Context.getUsuarioLogado().getId(), idUsuario));
		dto.setQuantidadeSeguidores(relacionamentoSeguidorService.contarSeguidores(idUsuario));
		dto.setQuantidadeSeguindo(relacionamentoSeguidorService.contarSeguindo(idUsuario));
		dto.setInstrumentosPerfil(buscarInstrumentosDoUsuario(idUsuario));
		// Address is private — strip before returning public profile
		dto.setEndPais(null);
		dto.setEndEstado(null);
		dto.setEndBairro(null);
		dto.setEndRua(null);
		dto.setEndNumero(null);
		dto.setEndCep(null);
		dto.setEndComplemento(null);
		return dto;
	}

	@Override
	public void reportarErro(String mensagem) {
		reporteDeErroService.reportarErro(mensagem, Context.getUsuarioLogado());
	}

	@Override
	public void enviarEmail(EmailDTO email) {
		emailService.enviarEmail(email.getAssunto(), email.getMensagem(), email.getDestinatario());
	}

    @Override
    public ResponseEntity<?> buscarProximosEventosDoUsuario() {
		EventosSeparadosDTO eventosSeparadosDTO = buscarProximosEventosAsync(Context.getUsuarioLogado().getId()).join();
        return ResponseEntity.ok(eventosSeparadosDTO);
    }

	@Override
	@Transactional(readOnly = true)
	public InfoPerfilUsuarioDTO buscarPorEmail(String email) {
		Usuario usuario = usuarioDao.findByEmailWithPublicacoes(email)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado para o email " + email));
		return new InfoPerfilUsuarioDTO(usuario);
	}

	@Override
	public ParticipacoesEspeciaisDTO buscarParticipacoesEspeciais() {
		return new ParticipacoesEspeciaisDTO(
				usuarioDao.buscarShowsEspeciais(Context.getUsuarioLogado().getId()),
				usuarioDao.buscarEnsaiosEspeciais(Context.getUsuarioLogado().getId())
		);
	}

	private CompletableFuture<EventosSeparadosDTO> buscarProximosEventosAsync(Long idUsuario) {
		return CompletableFuture
				.supplyAsync(() -> eventoHelperService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario));
	}

	private CompletableFuture<List<BandaParticipacaoEspecialDTO>> buscarParticipacoesEspeciaisAsync(Long idUsuario) {
		return CompletableFuture.supplyAsync(() -> bandaService.buscarParticipacoesEspeciais(idUsuario).stream()
				.map(BandaParticipacaoEspecialDTO::new).toList());
	}

//	private CompletableFuture<List<NotificacaoDTO>> buscarNotificacoesAsync(Long idUsuario) {
//		return CompletableFuture.supplyAsync(() -> notificacaoService.buscarNotificacoesPorUsuario(idUsuario).stream()
//				.map(new NotificacaoDTO()::toDTO).toList());
//	}

	private CompletableFuture<List<BandaDTO>> buscarBandasDoUsuarioAsync(Long idUsuario) {
		return CompletableFuture
				.supplyAsync(() -> bandaService.buscarBandasPorUsuario(idUsuario).stream()
						.map(banda -> new BandaDTO(banda, idUsuario)).toList());
	}

	@Override
	public List<BuscaGlobalDTO> buscarGlobal(String termo) {
		List<BuscaGlobalDTO> resultados = usuarioDao.buscarGlobal(termo).stream()
				.map(p -> new BuscaGlobalDTO(
						p.getId(),
						p.getNome(),
						com.baseapplication.core.enums.TipoParticipante.valueOf(p.getTipo()),
						p.getUrlFoto()
				)).toList();

		LevenshteinDistance levenshtein = new LevenshteinDistance();

		return resultados.stream()
				.sorted(Comparator.comparingInt(dto -> levenshtein.apply(termo.toLowerCase(), dto.getNome().toLowerCase())))
				.limit(20)
				.collect(Collectors.toList());
	}

	@Override
	public void seguirUsuario(Long idUsuario) {
		notificacaoService.enviarNotificacao(new NovoSeguidorEvent(Context.getUsuarioLogado(), idUsuario));
		relacionamentoSeguidorService.seguirUsuario(idUsuario);
	}

	@Override
	public void deixarDeSeguir(Long idUsuario) {
		relacionamentoSeguidorService.deixarDeSeguir(idUsuario);
	}

	@Override
	public TipoRelacionamento buscarTipoRelacionamento(Long idUsuario) {
		return relacionamentoSeguidorService.buscarTipoRelacionamento(Context.getUsuarioLogado().getId(), idUsuario);
	}

    @Override
    public List<InfoPerfilUsuarioDTO> buscarAmigos() {
        return relacionamentoSeguidorService.buscarAmigos().stream().map(InfoPerfilUsuarioDTO::new).toList();
    }

	@Override
	public List<com.baseapplication.core.dto.SeguidorDTO> buscarSeguidores(Long idUsuario) {
		return relacionamentoSeguidorService.buscarSeguidores(idUsuario).stream()
				.map(u -> new com.baseapplication.core.dto.SeguidorDTO(u.getId(), u.getNome(), u.getUrlFotoPerfil()))
				.toList();
	}

	@Override
	public List<com.baseapplication.core.dto.SeguidorDTO> buscarSeguindo(Long idUsuario) {
		return relacionamentoSeguidorService.buscarSeguindo(idUsuario).stream()
				.map(u -> new com.baseapplication.core.dto.SeguidorDTO(u.getId(), u.getNome(), u.getUrlFotoPerfil()))
				.toList();
	}

	@Override
	public List<InfoPerfilUsuarioDTO> buscarSugestoes(String termo) {
		List<Usuario> resultados = usuarioDao.buscarSugestoes(termo);

		LevenshteinDistance levenshtein = new LevenshteinDistance();

		resultados = resultados.stream()
				.sorted(Comparator.comparingInt(usuario -> levenshtein.apply(termo, usuario.getNome().toLowerCase())))
				.limit(20)
				.toList();

		return resultados.stream().map(InfoPerfilUsuarioDTO::resumo).toList();
	}

	@Override
	public List<InstrumentoDTO> buscarInstrumentosDoUsuario(Long idUsuario) {
		return instrumentoUsuarioDao.findByIdUsuarioOrderByFavoritoDescNomeAsc(idUsuario)
				.stream().map(InstrumentoDTO::new).toList();
	}

	@Override
	@jakarta.transaction.Transactional
	public InstrumentoDTO adicionarInstrumento(String nome) {
		Long idUsuario = Context.getUsuarioLogado().getId();
		InstrumentoUsuario instrumento = new InstrumentoUsuario(idUsuario, nome.trim(), false);
		return new InstrumentoDTO(instrumentoUsuarioDao.save(instrumento));
	}

	@Override
	@jakarta.transaction.Transactional
	public void removerInstrumento(Long idInstrumento) {
		Long idUsuario = Context.getUsuarioLogado().getId();
		InstrumentoUsuario instrumento = instrumentoUsuarioDao.findById(idInstrumento)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));
		if (!instrumento.getIdUsuario().equals(idUsuario))
			throw new InvalidParamException("Instrumento não pertence ao usuário");
		instrumentoUsuarioDao.delete(instrumento);
	}

	@Override
	@jakarta.transaction.Transactional
	public InstrumentoDTO definirInstrumentoFavorito(Long idInstrumento) {
		Long idUsuario = Context.getUsuarioLogado().getId();
		InstrumentoUsuario instrumento = instrumentoUsuarioDao.findById(idInstrumento)
				.orElseThrow(() -> new ResourceNotFoundException("Instrumento não encontrado"));
		if (!instrumento.getIdUsuario().equals(idUsuario))
			throw new InvalidParamException("Instrumento não pertence ao usuário");
		instrumentoUsuarioDao.clearFavoritosDoUsuario(idUsuario);
		instrumento.setFavorito(true);
		return new InstrumentoDTO(instrumentoUsuarioDao.save(instrumento));
	}

	@Override
	@jakarta.transaction.Transactional
	public void salvarInstrumentosRegistro(Long idUsuario, List<String> instrumentos, String instrumentoFavorito) {
		if (instrumentos == null || instrumentos.isEmpty()) return;
		for (String nome : instrumentos) {
			if (nome == null || nome.isBlank()) continue;
			boolean isFavorito = nome.trim().equalsIgnoreCase(instrumentoFavorito != null ? instrumentoFavorito.trim() : "");
			instrumentoUsuarioDao.save(new InstrumentoUsuario(idUsuario, nome.trim(), isFavorito));
		}
	}

	@Override
	@jakarta.transaction.Transactional
	public void atualizarTiposUsuario(List<String> tipos) {
		Usuario usuario = Context.getUsuarioLogado();
		usuario.setTiposUsuario(tipos != null ? tipos : new java.util.ArrayList<>());
		usuarioDao.save(usuario);
	}
}
