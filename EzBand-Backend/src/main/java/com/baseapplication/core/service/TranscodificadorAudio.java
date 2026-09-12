package com.baseapplication.core.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Converte o que o usuario enviou no que o navegador toca, preservando os canais separados.
 */
public interface TranscodificadorAudio {

    AudioProcessado processar(MultipartFile arquivo);

    /**
     * @param bytes       conteudo final, ja pronto para ir ao storage
     * @param contentType o que o MinIO devolve no GET -- e o que faz o {@code <audio>} tocar
     *                    em vez de baixar
     * @param extensao    com o ponto (".mp3"), para compor a chave do objeto
     * @param duracaoSeg  nulo quando o container nao declara duracao
     */
    record AudioProcessado(byte[] bytes, String contentType, String extensao, Integer duracaoSeg) {
    }
}
