package com.baseapplication.core.service;

import com.baseapplication.core.dto.NovaPublicacaoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PublicacaoService {
    void publicar(NovaPublicacaoDTO publicacao, List<MultipartFile> imagem);

    void editarTextoPublicacao(Long idPublicacao, String texto);

    void excluirPublicacao(Long idPublicacao);
}
