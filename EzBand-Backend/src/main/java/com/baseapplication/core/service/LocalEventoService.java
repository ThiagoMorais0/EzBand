package com.baseapplication.core.service;

import com.baseapplication.core.dto.CadastroLocalEventoDTO;
import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.model.dto.ShowDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LocalEventoService {
    LocalEvento cadastrar(LocalEvento toEntity);

    List<LocalEvento> buscarLocalEventosDoUsuario();

    List<LocalEvento> buscarTodos();

    void deletarTodos();

    LocalEvento buscarPorId(Long id);

    void editar(LocalEventoDTO estudioDTO);

    List<ShowDTO> buscarShowsPorLocalEvento(Long idLocalEvento);

    ResponseEntity<?> cadastrarComImagem(CadastroLocalEventoDTO localEvento, MultipartFile imagem);
}
