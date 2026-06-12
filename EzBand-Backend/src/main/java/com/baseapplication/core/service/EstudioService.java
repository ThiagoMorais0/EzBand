package com.baseapplication.core.service;

import com.baseapplication.core.dto.BuscaEstudioDTO;
import com.baseapplication.core.dto.CadastroEstudioDTO;
import com.baseapplication.core.dto.EquipamentoEstudioDTO;
import com.baseapplication.core.dto.EstudioDTO;
import com.baseapplication.core.dto.ServicoEstudioDTO;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.dto.EnsaioDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.EnsaioEstudioDTO;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public interface EstudioService {
    Estudio cadastrar(Estudio toEntity);

    List<Estudio> buscarEstudiosDoUsuario();

    List<Estudio> buscarTodos();

    void deletarTodos();

    Estudio buscarPorId(Long id);

    void editar(EstudioDTO estudioDTO);

    ResponseEntity<?> cadastrarComImagem(CadastroEstudioDTO estudio, MultipartFile imagem);

    void editarComImagem(String estudioJson, MultipartFile imagem);

    List<EnsaioDTO> buscarEnsaiosPorEstudio(Long idEstudio);

    List<EstudioDTO> buscarSugestoes(BuscaEstudioDTO dto);

    void deletar(Long idEstudio);

    ResponseEntity<?> adicionarSocio(Long idEstudio, Long idUsuario);

    ResponseEntity<?> removerSocio(Long idEstudio, Long idUsuario);

    ResponseEntity<?> cadastrarComImagemRetornandoId(CadastroEstudioDTO estudio, MultipartFile imagem);

    List<EstudioDTO> buscarEstudiosDoUsuarioDTO();

    EstudioDTO buscarPorIdDTO(Long id);

    List<EstudioDTO> buscarTodosDTO();

    ServicoEstudioDTO adicionarServico(Long idEstudio, ServicoEstudioDTO dto);

    void editarServico(ServicoEstudioDTO dto);

    void removerServico(Long idServico);

    EquipamentoEstudioDTO adicionarEquipamento(Long idEstudio, String dadosJson, MultipartFile imagem);

    void editarEquipamento(Long idEquipamento, String dadosJson, MultipartFile imagem);

    void removerEquipamento(Long idEquipamento);

    List<EnsaioDTO> buscarEnsaiosPendentesPorEstudio(Long idEstudio);

    List<EnsaioDTO> buscarEnsaiosAguardandoAprovacaoPorEstudio(Long idEstudio);

    List<EnsaioDTO> buscarEnsaiosHistoricoPorEstudio(Long idEstudio);

    void aprovarEnsaio(Long idEnsaio);

    void recusarEnsaio(Long idEnsaio, String motivo);

    void cancelarEnsaioComoEstudio(Long idEnsaio, String motivo);

    Map<String, List<EnsaioEstudioDTO>> buscarAgendaEstudioPorMes(int ano, int mes);

    Map<String, List<EnsaioEstudioDTO>> buscarProximosEventosEstudios();
}
