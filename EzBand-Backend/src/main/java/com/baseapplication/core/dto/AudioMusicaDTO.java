package com.baseapplication.core.dto;

import com.baseapplication.core.model.AudioMusica;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * O VS como a tela precisa dele. Vai sempre em lista, por banda: o front casa cada
 * {@code chaveMusica} com a musica do repertorio em memoria, o que resolve o setlist inteiro
 * numa requisicao em vez de uma por faixa.
 */
@Getter
@Setter
@NoArgsConstructor
public class AudioMusicaDTO {

    private Long id;
    private String chaveMusica;
    private String titulo;
    private String artista;
    private String url;
    private String nomeOriginal;
    private Integer duracaoSeg;
    private Long tamanhoBytes;

    public AudioMusicaDTO(AudioMusica entity) {
        this.id = entity.getId();
        this.chaveMusica = entity.getChaveMusica();
        this.titulo = entity.getTitulo();
        this.artista = entity.getArtista();
        this.url = entity.getUrl();
        this.nomeOriginal = entity.getNomeOriginal();
        this.duracaoSeg = entity.getDuracaoSeg();
        this.tamanhoBytes = entity.getTamanhoBytes();
    }
}
