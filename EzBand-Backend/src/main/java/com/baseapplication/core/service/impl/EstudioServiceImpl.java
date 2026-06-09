package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.dao.EquipamentoEstudioDao;
import com.baseapplication.core.dao.EstudioDao;
import com.baseapplication.core.dao.ServicoEstudioDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.event.events.EnsaioAprovadoPeloEstudioEvent;
import com.baseapplication.core.event.events.EnsaioCanceladoPeloEstudioEvent;
import com.baseapplication.core.event.events.EnsaioRecusadoPeloEstudioEvent;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.EquipamentoEstudio;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.ServicoEstudio;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.embedded.Endereco;
import com.baseapplication.core.service.EnsaioService;
import com.baseapplication.core.service.EstudioService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EstudioServiceImpl implements EstudioService {

    private final EstudioDao dao;
    private final UsuarioDao usuarioDao;
    private final ServicoEstudioDao servicoDao;
    private final EquipamentoEstudioDao equipamentoDao;
    private final ImagemService imagemService;
    private final EnsaioService ensaioService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Estudio cadastrar(Estudio estudio) {
        return dao.save(estudio);
    }

    @Override
    public List<Estudio> buscarEstudiosDoUsuario() {
        Long idUsuario = Context.getUsuarioLogado().getId();
        List<Estudio> comProprietario = dao.findByProprietario(idUsuario);
        List<Estudio> comoSocio = dao.findBySocio(idUsuario);
        List<Estudio> todos = new ArrayList<>(comProprietario);
        comoSocio.stream().filter(e -> todos.stream().noneMatch(ex -> ex.getId().equals(e.getId()))).forEach(todos::add);
        return todos;
    }

    @Override
    public List<Estudio> buscarTodos() {
        return dao.findAll();
    }

    @Override
    public void deletarTodos() {
        dao.deleteAll();
    }

    @Override
    public Estudio buscarPorId(Long id) {
        return dao.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void editar(EstudioDTO estudioDTO) {
        Estudio estudio = dao.findById(estudioDTO.getId())
                .orElseThrow(() -> new InternalException("Estúdio não encontrado"));

        estudio.setNome(estudioDTO.getNome());
        estudio.setDescricao(estudioDTO.getDescricao());
        estudio.setExigirConfirmacaoEnsaios(estudioDTO.isExigirConfirmacaoEnsaios());

        if (estudioDTO.getDiasFuncionamento() != null)
            estudio.setDiasFuncionamento(new ArrayList<>(estudioDTO.getDiasFuncionamento()));

        if (estudioDTO.getHorarioInicioFuncionamento() != null && !estudioDTO.getHorarioInicioFuncionamento().isBlank())
            estudio.setHorarioInicioFuncionamento(LocalDateTime.parse(estudioDTO.getHorarioInicioFuncionamento()));
        if (estudioDTO.getHorarioFinalFuncionamento() != null && !estudioDTO.getHorarioFinalFuncionamento().isBlank())
            estudio.setHorarioFinalFuncionamento(LocalDateTime.parse(estudioDTO.getHorarioFinalFuncionamento()));

        if (estudioDTO.getEndereco() != null) {
            if (estudio.getEndereco() == null) estudio.setEndereco(new Endereco());
            BeanUtils.copyProperties(estudioDTO.getEndereco(), estudio.getEndereco());
        }

        dao.save(estudio);
    }

    @Override
    @Transactional
    public ResponseEntity<?> cadastrarComImagem(CadastroEstudioDTO estudioDTO, MultipartFile imagem) {
        return cadastrarComImagemRetornandoId(estudioDTO, imagem);
    }

    @Override
    @Transactional
    public ResponseEntity<?> cadastrarComImagemRetornandoId(CadastroEstudioDTO estudioDTO, MultipartFile imagem) {
        Estudio estudio = estudioDTO.toEntity();
        estudio = cadastrar(estudio);
        if (imagem != null && !imagem.isEmpty()) {
            String urlImagem = imagemService.saveImageAndGetUrl(imagem, "studiologo", estudio.getId() + "." + FileUtils.getSufix(imagem));
            estudio.setUrlFotoPerfil(urlImagem);
            estudio = cadastrar(estudio);
        }
        return ResponseEntity.ok(estudio.getId());
    }

    @Override
    @Transactional
    public void editarComImagem(String estudioJson, MultipartFile imagem) {
        EstudioDTO estudioDTO;
        try {
            estudioDTO = new ObjectMapper().readValue(estudioJson, EstudioDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
        Estudio estudio = dao.findById(estudioDTO.getId())
                .orElseThrow(() -> new InternalException("Estúdio não encontrado"));
        if (estudio.getUrlFotoPerfil() != null)
            imagemService.deletarImagemPorUrl(estudio.getUrlFotoPerfil());
        String urlImagem = imagemService.saveImageAndGetUrl(imagem, "studiologo", estudioDTO.getId() + "." + FileUtils.getSufix(imagem));
        estudio.setUrlFotoPerfil(urlImagem);
        dao.save(estudio);
    }

    @Override
    @Transactional
    public List<EnsaioDTO> buscarEnsaiosPorEstudio(Long idEstudio) {
        Estudio estudio = buscarPorId(idEstudio);
        if(estudio == null){
            throw new InternalException("Estúdio não encontrado");
        }
        return estudio.getEnsaios().stream().map(EnsaioDTO::new).toList();
    }

    @Override
    public void deletar(Long idEstudio) {
        Estudio estudio = dao.findById(idEstudio)
                .orElseThrow(() -> new InternalException("Estúdio não encontrado"));
        dao.delete(estudio);
    }

    @Override
    @Transactional
    public ResponseEntity<?> adicionarSocio(Long idEstudio, Long idUsuario) {
        Estudio estudio = dao.findById(idEstudio).orElse(null);
        Usuario usuario = usuarioDao.findById(idUsuario).orElse(null);
        if (estudio == null || usuario == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        if (estudio.getSocios().stream().anyMatch(s -> s.getId().equals(idUsuario)))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuário já é sócio");
        estudio.getSocios().add(usuario);
        dao.save(estudio);
        return ResponseEntity.ok("Sócio adicionado");
    }

    @Override
    @Transactional
    public ResponseEntity<?> removerSocio(Long idEstudio, Long idUsuario) {
        Estudio estudio = dao.findById(idEstudio).orElse(null);
        if (estudio == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Não encontrado");
        estudio.getSocios().removeIf(s -> s.getId().equals(idUsuario));
        dao.save(estudio);
        return ResponseEntity.ok("Sócio removido");
    }

    @Override
    @Transactional
    public List<EstudioDTO> buscarEstudiosDoUsuarioDTO() {
        return buscarEstudiosDoUsuario().stream().map(EstudioDTO::new).toList();
    }

    @Override
    @Transactional
    public EstudioDTO buscarPorIdDTO(Long id) {
        return new EstudioDTO(dao.findById(id).orElse(new Estudio()));
    }

    @Override
    @Transactional
    public List<EstudioDTO> buscarTodosDTO() {
        return dao.findAll().stream().map(EstudioDTO::new).toList();
    }

    @Override
    @Transactional
    public ServicoEstudioDTO adicionarServico(Long idEstudio, ServicoEstudioDTO dto) {
        Estudio estudio = dao.findById(idEstudio)
                .orElseThrow(() -> new InternalException("Estúdio não encontrado"));
        ServicoEstudio servico = new ServicoEstudio();
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setValor(dto.getValor());
        servico.setUnidade(dto.getUnidade());
        servico.setEstudio(estudio);
        return new ServicoEstudioDTO(servicoDao.save(servico));
    }

    @Override
    @Transactional
    public void editarServico(ServicoEstudioDTO dto) {
        ServicoEstudio servico = servicoDao.findById(dto.getId())
                .orElseThrow(() -> new InternalException("Serviço não encontrado"));
        servico.setNome(dto.getNome());
        servico.setDescricao(dto.getDescricao());
        servico.setValor(dto.getValor());
        servico.setUnidade(dto.getUnidade());
        servicoDao.save(servico);
    }

    @Override
    @Transactional
    public void removerServico(Long idServico) {
        servicoDao.deleteById(idServico);
    }

    @Override
    @Transactional
    public EquipamentoEstudioDTO adicionarEquipamento(Long idEstudio, String dadosJson, MultipartFile imagem) {
        EquipamentoEstudioDTO dadosDTO;
        try {
            dadosDTO = new ObjectMapper().readValue(dadosJson, EquipamentoEstudioDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
        Estudio estudio = dao.findById(idEstudio)
                .orElseThrow(() -> new InternalException("Estúdio não encontrado"));
        EquipamentoEstudio eq = new EquipamentoEstudio();
        eq.setMarca(dadosDTO.getMarca());
        eq.setModelo(dadosDTO.getModelo());
        eq.setQuantidade(dadosDTO.getQuantidade());
        eq.setAtivo(dadosDTO.getAtivo() != null ? dadosDTO.getAtivo() : true);
        eq.setObservacao(dadosDTO.getObservacao());
        eq.setEstudio(estudio);
        EquipamentoEstudio saved = equipamentoDao.save(eq);
        if (imagem != null && !imagem.isEmpty()) {
            String urlFoto = imagemService.saveImageAndGetUrl(imagem, "equipamento", saved.getId() + "." + FileUtils.getSufix(imagem));
            saved.setUrlFoto(urlFoto);
            saved = equipamentoDao.save(saved);
        }
        return new EquipamentoEstudioDTO(saved);
    }

    @Override
    @Transactional
    public void editarEquipamento(Long idEquipamento, String dadosJson, MultipartFile imagem) {
        EquipamentoEstudioDTO dadosDTO;
        try {
            dadosDTO = new ObjectMapper().readValue(dadosJson, EquipamentoEstudioDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
        EquipamentoEstudio eq = equipamentoDao.findById(idEquipamento)
                .orElseThrow(() -> new InternalException("Equipamento não encontrado"));
        eq.setMarca(dadosDTO.getMarca());
        eq.setModelo(dadosDTO.getModelo());
        eq.setQuantidade(dadosDTO.getQuantidade());
        eq.setAtivo(dadosDTO.getAtivo() != null ? dadosDTO.getAtivo() : eq.getAtivo());
        eq.setObservacao(dadosDTO.getObservacao());
        if (imagem != null && !imagem.isEmpty()) {
            if (eq.getUrlFoto() != null) imagemService.deletarImagemPorUrl(eq.getUrlFoto());
            String urlFoto = imagemService.saveImageAndGetUrl(imagem, "equipamento", eq.getId() + "." + FileUtils.getSufix(imagem));
            eq.setUrlFoto(urlFoto);
        }
        equipamentoDao.save(eq);
    }

    @Override
    @Transactional
    public void removerEquipamento(Long idEquipamento) {
        EquipamentoEstudio eq = equipamentoDao.findById(idEquipamento)
                .orElseThrow(() -> new InternalException("Equipamento não encontrado"));
        if (eq.getUrlFoto() != null) imagemService.deletarImagemPorUrl(eq.getUrlFoto());
        equipamentoDao.delete(eq);
    }

    @Override
    @Transactional
    public List<EnsaioDTO> buscarEnsaiosPendentesPorEstudio(Long idEstudio) {
        Estudio estudio = buscarPorId(idEstudio);
        if (estudio == null) throw new InternalException("Estúdio não encontrado");
        return estudio.getEnsaios().stream()
                .filter(e -> StatusEvento.PENDENTE.equals(e.getStatus()))
                .sorted(Comparator.comparing(Ensaio::getData, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(EnsaioDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public List<EnsaioDTO> buscarEnsaiosAguardandoAprovacaoPorEstudio(Long idEstudio) {
        Estudio estudio = buscarPorId(idEstudio);
        if (estudio == null) throw new InternalException("Estúdio não encontrado");
        return estudio.getEnsaios().stream()
                .filter(e -> StatusEvento.AGUARDANDO_APROVACAO.equals(e.getStatus()))
                .sorted(Comparator.comparing(Ensaio::getData, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(EnsaioDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public List<EnsaioDTO> buscarEnsaiosHistoricoPorEstudio(Long idEstudio) {
        Estudio estudio = buscarPorId(idEstudio);
        if (estudio == null) throw new InternalException("Estúdio não encontrado");
        return estudio.getEnsaios().stream()
                .filter(e -> StatusEvento.REALIZADO.equals(e.getStatus()) || StatusEvento.CANCELADO.equals(e.getStatus()))
                .sorted(Comparator.comparing(Ensaio::getData, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(EnsaioDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public void aprovarEnsaio(Long idEnsaio) {
        Ensaio ensaio = ensaioService.buscarPorId(idEnsaio);
        Estudio estudio = ensaio.getEstudio();
        ensaioService.alterarStatus(idEnsaio, StatusEvento.PENDENTE);
        eventPublisher.publishEvent(new EnsaioAprovadoPeloEstudioEvent(ensaio, estudio));
    }

    @Override
    @Transactional
    public void recusarEnsaio(Long idEnsaio, String motivo) {
        Ensaio ensaio = ensaioService.buscarPorId(idEnsaio);
        Estudio estudio = ensaio.getEstudio();
        ensaioService.alterarStatus(idEnsaio, StatusEvento.CANCELADO);
        eventPublisher.publishEvent(new EnsaioRecusadoPeloEstudioEvent(ensaio, estudio, motivo));
    }

    @Override
    @Transactional
    public void cancelarEnsaioComoEstudio(Long idEnsaio, String motivo) {
        Ensaio ensaio = ensaioService.buscarPorId(idEnsaio);
        Estudio estudio = ensaio.getEstudio();
        ensaioService.alterarStatus(idEnsaio, StatusEvento.CANCELADO);
        eventPublisher.publishEvent(new EnsaioCanceladoPeloEstudioEvent(ensaio, estudio, motivo));
    }

    @Override
    @Transactional
    public List<EstudioDTO> buscarSugestoes(BuscaEstudioDTO dto) {
        List<Estudio> resultados = dao.buscarSugestoes(dto);

        String searchNome = dto.getNome() != null ? dto.getNome().toLowerCase() : "";

        Stream<Estudio> stream = resultados.stream();
        if (!searchNome.isEmpty()) {
            LevenshteinDistance levenshtein = new LevenshteinDistance();
            stream = stream.sorted(Comparator.comparingInt(estudio -> {
                String estudioNome = estudio.getNome() != null ? estudio.getNome().toLowerCase() : "";
                return levenshtein.apply(searchNome, estudioNome);
            }));
        }

        return stream.limit(20).map(EstudioDTO::new).collect(Collectors.toList());
    }
}
