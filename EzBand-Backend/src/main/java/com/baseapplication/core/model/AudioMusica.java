package com.baseapplication.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * O VS (virtual soundcheck) de uma musica: arquivo de audio com click de um lado e
 * playback do outro, que o baterista abre no fone.
 *
 * <p>Fica fora do {@link com.baseapplication.core.model.embedded.Musica} de proposito.
 * Aquele embutido e copiado por valor para cada evento, entao um ponteiro la dentro
 * viraria N ponteiros para o mesmo arquivo. Aqui o vinculo e
 * ({@code idBanda}, {@code chaveMusica}) -- ver {@link com.baseapplication.core.utils.ChaveMusica}.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "AUDIO_MUSICA")
public class AudioMusica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_banda", nullable = false)
    private Long idBanda;

    /** titulo|artista normalizado. Ver {@link com.baseapplication.core.utils.ChaveMusica}. */
    @Column(name = "chave_musica", nullable = false, length = 300)
    private String chaveMusica;

    /**
     * Titulo e artista como estavam no upload. Nao sao a chave -- servem para a tela de
     * gerenciamento mostrar a que musica o arquivo pertence sem cruzar com o repertorio,
     * e para o usuario reconhecer um audio cuja musica foi renomeada.
     */
    @Column(name = "titulo", length = 200)
    private String titulo;

    @Column(name = "artista", length = 200)
    private String artista;

    @Column(name = "url", nullable = false, length = 500)
    private String url;

    @Column(name = "nome_original", length = 255)
    private String nomeOriginal;

    @Column(name = "duracao_seg")
    private Integer duracaoSeg;

    @Column(name = "tamanho_bytes", nullable = false)
    private Long tamanhoBytes = 0L;

    /** Hash do arquivo ja transcodificado. Dois uploads iguais reaproveitam o objeto. */
    @Column(name = "sha256", length = 64)
    private String sha256;

    @Column(name = "id_usuario_upload")
    private Long idUsuarioUpload;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();
}
