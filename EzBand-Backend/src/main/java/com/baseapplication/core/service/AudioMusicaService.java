package com.baseapplication.core.service;

import com.baseapplication.core.dto.AudioMusicaDTO;
import com.baseapplication.core.dto.QuotaAudioDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AudioMusicaService {

    /** Todos os VS da banda. A tela casa por {@code chaveMusica}; nao ha busca por musica. */
    List<AudioMusicaDTO> listarPorBanda(Long idBanda);

    QuotaAudioDTO consultarQuota(Long idBanda);

    AudioMusicaDTO enviar(Long idBanda, String titulo, String artista, MultipartFile arquivo);

    void remover(Long idBanda, Long idAudio);

    /**
     * Acompanha o VS quando a musica e renomeada no repertorio. Sem isto a chave antiga fica
     * orfa e o audio some da tela sem erro nenhum.
     */
    void renomear(Long idBanda, String tituloAntigo, String artistaAntigo,
                  String tituloNovo, String artistaNovo);
}
