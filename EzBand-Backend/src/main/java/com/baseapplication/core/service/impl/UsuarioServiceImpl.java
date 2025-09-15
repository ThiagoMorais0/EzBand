package com.baseapplication.core.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.event.events.SolicitacaoIngressarBandaEvent;
import com.baseapplication.core.model.Banda;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.enums.TipoContato;
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
	private final BandaService bandaService;
	private final NotificacaoService notificacaoService;
	private final EventoHelperService eventoHelperService;
	private final ImagemService imagemService;
	private final ReporteDeErroService reporteDeErroService;
	private final EmailService emailService;

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
		if(imagem != null){
			 urlImagem = imagemService.saveImageAndGetUrl(imagem, "profilepictures", usuario.getId() + "." + FileUtils.getSufix(imagem));
		}else if(removerImagemDePerfil){
			urlImagem = "default";
		}
		BeanUtils.copyProperties(usuarioDTO, usuario);
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


		notificacaoService.enviarNotificacao(new SolicitacaoIngressarBandaEvent(
				Context.getUsuarioLogado(), banda, instrumento
		));
	}

	@Override
	public InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(Long idUsuario) {
		return new InfoPerfilUsuarioDTO(buscarPorId(idUsuario));
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
	public InfoPerfilUsuarioDTO buscarPorEmail(String email) {
		return new InfoPerfilUsuarioDTO(findByEmail(email));
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
				.supplyAsync(() -> bandaService.buscarBandasPorUsuario(idUsuario).stream().map(BandaDTO::new).toList());
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
}
