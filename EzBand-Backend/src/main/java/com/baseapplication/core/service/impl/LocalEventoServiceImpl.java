package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.EquipamentoLocalEventoDao;
import com.baseapplication.core.dao.LocalEventoDao;
import com.baseapplication.core.dao.SocioLocalEventoDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.BuscaLocalEventoDTO;
import com.baseapplication.core.dto.CadastroLocalEventoDTO;
import com.baseapplication.core.dto.EquipamentoLocalEventoDTO;
import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.event.events.ShowAprovadoPeloLocalEventoEvent;
import com.baseapplication.core.event.events.ShowRecusadoPeloLocalEventoEvent;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.embedded.Endereco;
import com.baseapplication.core.service.*;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalEventoServiceImpl implements LocalEventoService {

    private final LocalEventoDao dao;
    private final SocioLocalEventoDao socioDao;
    private final EquipamentoLocalEventoDao equipamentoDao;
    private final UsuarioDao usuarioDao;
    private final ImagemService imagemService;
    private final ShowService showService;
    private final NotificacaoService notificacaoService;

    @Override
    public LocalEvento cadastrar(LocalEvento localEvento) {
        return dao.save(localEvento);
    }

    @Override
    @Transactional
    public ResponseEntity<?> cadastrarComImagem(CadastroLocalEventoDTO dto, MultipartFile imagem) {
        LocalEvento localEvento = dto.toEntity();
        localEvento = cadastrar(localEvento);
        if (imagem != null && !imagem.isEmpty()) {
            String url = imagemService.saveImageAndGetUrl(imagem, "venuelogo", localEvento.getId() + "." + FileUtils.getSufix(imagem));
            localEvento.setUrlFotoPerfil(url);
            localEvento = cadastrar(localEvento);
        }
        return ResponseEntity.ok(localEvento.getId());
    }

    @Override
    @Transactional
    public void editar(LocalEventoDTO dto) {
        LocalEvento entity = dao.findById(dto.getId()).orElseThrow(() -> new InternalException("Local não encontrado"));
        dto.toEntity(entity);
        dao.save(entity);
    }

    @Override
    public void deletar(Long id) {
        LocalEvento entity = dao.findById(id).orElseThrow(() -> new InternalException("Local não encontrado"));
        dao.delete(entity);
    }

    @Override
    public void deletarTodos() {
        dao.deleteAll();
    }

    @Override
    @Transactional
    public List<LocalEventoDTO> buscarLocalEventosDoUsuario() {
        Long idUsuario = Context.getUsuarioLogado().getId();
        List<LocalEvento> comoProprietario = dao.findByProprietario(idUsuario);
        List<LocalEvento> comoSocio = dao.findBySocio(idUsuario);
        List<LocalEvento> todos = new ArrayList<>(comoProprietario);
        comoSocio.stream()
                .filter(e -> todos.stream().noneMatch(ex -> ex.getId().equals(e.getId())))
                .forEach(todos::add);
        return todos.stream().map(LocalEventoDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<LocalEventoDTO> buscarTodos() {
        return dao.findAll().stream().map(LocalEventoDTO::new).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LocalEventoDTO buscarPorId(Long id) {
        return dao.findById(id).map(LocalEventoDTO::new).orElse(null);
    }

    @Override
    public LocalEvento buscarEntidadePorId(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public List<LocalEventoDTO> buscarSugestoes(BuscaLocalEventoDTO dto) {
        List<LocalEvento> resultados = dao.buscarSugestoes(dto);
        LevenshteinDistance levenshtein = new LevenshteinDistance();
        return resultados.stream()
                .sorted(Comparator.comparingInt(le ->
                        dto.getNome() != null && !dto.getNome().isEmpty()
                                ? levenshtein.apply(dto.getNome().toLowerCase(), le.getNome().toLowerCase())
                                : 0))
                .limit(20)
                .map(LocalEventoDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ShowDTO> buscarShowsPorLocalEvento(Long idLocalEvento) {
        LocalEvento le = dao.findById(idLocalEvento).orElseThrow();
        return le.getShows().stream()
                .filter(s -> s.getData() != null)
                .map(ShowDTO::new)
                .sorted(Comparator.comparing(ShowDTO::getData))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ShowDTO> buscarShowsAguardandoAprovacao(Long idLocalEvento) {
        LocalEvento le = dao.findById(idLocalEvento).orElseThrow();
        return le.getShows().stream()
                .filter(s -> StatusEvento.AGUARDANDO_APROVACAO.equals(s.getStatus()))
                .map(ShowDTO::new)
                .sorted(Comparator.comparing(ShowDTO::getData, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ShowDTO> buscarShowsHistorico(Long idLocalEvento) {
        LocalEvento le = dao.findById(idLocalEvento).orElseThrow();
        return le.getShows().stream()
                .filter(s -> !StatusEvento.AGUARDANDO_APROVACAO.equals(s.getStatus()))
                .map(ShowDTO::new)
                .sorted(Comparator.comparing(ShowDTO::getData, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EquipamentoLocalEventoDTO adicionarEquipamento(Long idLocalEvento, String dadosJson, MultipartFile imagem) {
        EquipamentoLocalEventoDTO dadosDTO;
        try {
            dadosDTO = new ObjectMapper().readValue(dadosJson, EquipamentoLocalEventoDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
        LocalEvento le = dao.findById(idLocalEvento).orElseThrow(() -> new InternalException("Local não encontrado"));
        EquipamentoLocalEvento eq = new EquipamentoLocalEvento();
        eq.setMarca(dadosDTO.getMarca());
        eq.setModelo(dadosDTO.getModelo());
        eq.setQuantidade(dadosDTO.getQuantidade());
        eq.setAtivo(dadosDTO.getAtivo() != null ? dadosDTO.getAtivo() : true);
        eq.setObservacao(dadosDTO.getObservacao());
        eq.setLocalEvento(le);
        EquipamentoLocalEvento saved = equipamentoDao.save(eq);
        if (imagem != null && !imagem.isEmpty()) {
            String url = imagemService.saveImageAndGetUrl(imagem, "equipamento", saved.getId() + "." + FileUtils.getSufix(imagem));
            saved.setUrlFoto(url);
            saved = equipamentoDao.save(saved);
        }
        return new EquipamentoLocalEventoDTO(saved);
    }

    @Override
    @Transactional
    public void editarEquipamento(Long idEquipamento, String dadosJson, MultipartFile imagem) {
        EquipamentoLocalEventoDTO dadosDTO;
        try {
            dadosDTO = new ObjectMapper().readValue(dadosJson, EquipamentoLocalEventoDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
        EquipamentoLocalEvento eq = equipamentoDao.findById(idEquipamento)
                .orElseThrow(() -> new InternalException("Equipamento não encontrado"));
        eq.setMarca(dadosDTO.getMarca());
        eq.setModelo(dadosDTO.getModelo());
        eq.setQuantidade(dadosDTO.getQuantidade());
        eq.setAtivo(dadosDTO.getAtivo());
        eq.setObservacao(dadosDTO.getObservacao());
        if (imagem != null && !imagem.isEmpty()) {
            if (eq.getUrlFoto() != null) imagemService.deletarImagemPorUrl(eq.getUrlFoto());
            String url = imagemService.saveImageAndGetUrl(imagem, "equipamento", idEquipamento + "." + FileUtils.getSufix(imagem));
            eq.setUrlFoto(url);
        }
        equipamentoDao.save(eq);
    }

    @Override
    public void removerEquipamento(Long idEquipamento) {
        equipamentoDao.deleteById(idEquipamento);
    }

    @Override
    @Transactional
    public ResponseEntity<?> adicionarSocio(Long idLocalEvento, Long idUsuario) {
        LocalEvento le = dao.findById(idLocalEvento).orElse(null);
        Usuario usuario = usuarioDao.findById(idUsuario).orElse(null);
        if (le == null || usuario == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        boolean jaExiste = le.getSocios().stream()
                .anyMatch(s -> s.getUsuario().getId().equals(idUsuario));
        if (jaExiste) return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já é sócio");
        SocioLocalEvento socio = new SocioLocalEvento();
        socio.setId(new SocioLocalEventoId(idLocalEvento, idUsuario));
        socio.setLocalEvento(le);
        socio.setUsuario(usuario);
        socio.setAprovaShows(false);
        socioDao.save(socio);
        return ResponseEntity.ok("Sócio adicionado");
    }

    @Override
    @Transactional
    public ResponseEntity<?> removerSocio(Long idLocalEvento, Long idUsuario) {
        SocioLocalEventoId pk = new SocioLocalEventoId(idLocalEvento, idUsuario);
        if (!socioDao.existsById(pk)) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Sócio não encontrado");
        socioDao.deleteById(pk);
        return ResponseEntity.ok("Sócio removido");
    }

    @Override
    @Transactional
    public ResponseEntity<?> editarPermissaoSocio(Long idLocalEvento, Long idUsuario, Boolean aprovaShows) {
        SocioLocalEventoId pk = new SocioLocalEventoId(idLocalEvento, idUsuario);
        SocioLocalEvento socio = socioDao.findById(pk)
                .orElseThrow(() -> new InternalException("Sócio não encontrado"));
        socio.setAprovaShows(aprovaShows);
        socioDao.save(socio);
        return ResponseEntity.ok("Permissão atualizada");
    }

    @Override
    @Transactional
    public ResponseEntity<?> aprovarShow(Long idShow) {
        Show show = showService.buscarPorId(idShow);
        if (show == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Show não encontrado");
        if (!podeGerenciarShow(show)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão");
        show.setStatus(StatusEvento.PENDENTE);
        showService.salvar(show);
        notificacaoService.enviarNotificacao(new ShowAprovadoPeloLocalEventoEvent(show));
        return ResponseEntity.ok("Show aprovado");
    }

    @Override
    @Transactional
    public ResponseEntity<?> recusarShow(Long idShow, String motivo) {
        Show show = showService.buscarPorId(idShow);
        if (show == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Show não encontrado");
        if (!podeGerenciarShow(show)) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Sem permissão");
        show.setStatus(StatusEvento.CANCELADO);
        showService.salvar(show);
        notificacaoService.enviarNotificacao(new ShowRecusadoPeloLocalEventoEvent(show, motivo));
        return ResponseEntity.ok("Show recusado");
    }

    private boolean podeGerenciarShow(Show show) {
        if (show.getLocalEvento() == null) return false;
        Long idUsuarioLogado = Context.getUsuarioLogado().getId();
        LocalEvento le = show.getLocalEvento();
        if (le.getProprietario().getId().equals(idUsuarioLogado)) return true;
        return le.getSocios().stream()
                .anyMatch(s -> s.getUsuario().getId().equals(idUsuarioLogado) && Boolean.TRUE.equals(s.getAprovaShows()));
    }
}
