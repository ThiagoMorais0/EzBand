package com.baseapplication.core.service;

import com.baseapplication.core.dto.BuscaLocalEventoDTO;
import com.baseapplication.core.dto.CadastroLocalEventoDTO;
import com.baseapplication.core.dto.EquipamentoLocalEventoDTO;
import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.model.dto.ShowDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LocalEventoService {
    LocalEvento cadastrar(LocalEvento localEvento);
    ResponseEntity<?> cadastrarComImagem(CadastroLocalEventoDTO dto, MultipartFile imagem);
    void editar(LocalEventoDTO dto);
    void deletar(Long id);
    void deletarTodos();

    List<LocalEventoDTO> buscarLocalEventosDoUsuario();
    List<LocalEventoDTO> buscarTodos();
    LocalEventoDTO buscarPorId(Long id);
    LocalEvento buscarEntidadePorId(Long id);
    List<LocalEventoDTO> buscarSugestoes(BuscaLocalEventoDTO dto);

    List<ShowDTO> buscarShowsPorLocalEvento(Long idLocalEvento);
    List<ShowDTO> buscarShowsAguardandoAprovacao(Long idLocalEvento);
    List<ShowDTO> buscarShowsHistorico(Long idLocalEvento);

    EquipamentoLocalEventoDTO adicionarEquipamento(Long idLocalEvento, String dadosJson, MultipartFile imagem);
    void editarEquipamento(Long idEquipamento, String dadosJson, MultipartFile imagem);
    void removerEquipamento(Long idEquipamento);

    ResponseEntity<?> adicionarSocio(Long idLocalEvento, Long idUsuario);
    ResponseEntity<?> removerSocio(Long idLocalEvento, Long idUsuario);
    ResponseEntity<?> editarPermissaoSocio(Long idLocalEvento, Long idUsuario, Boolean aprovaShows);

    ResponseEntity<?> aprovarShow(Long idShow);
    ResponseEntity<?> recusarShow(Long idShow, String motivo);
}
