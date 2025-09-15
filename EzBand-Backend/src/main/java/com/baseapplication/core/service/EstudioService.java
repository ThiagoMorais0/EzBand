package com.baseapplication.core.service;

import com.baseapplication.core.dto.CadastroEstudioDTO;
import com.baseapplication.core.dto.EstudioDTO;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.dto.EnsaioDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
}
