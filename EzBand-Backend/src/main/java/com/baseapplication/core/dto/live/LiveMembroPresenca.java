package com.baseapplication.core.dto.live;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Um músico conectado à sessão ao vivo.
 *
 * <p>A fonte da presença são os sockets abertos no {@code LiveSessionRegistry}, não o
 * store: se a conexão caiu, o músico não está no palco olhando a tela, mesmo que o
 * snapshot ainda exista.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiveMembroPresenca implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUsuario;
    private String nome;
    private String urlFotoPerfil;
    private String instrumentos;

    /** Instante (epoch ms) em que entrou na sessão. */
    private Long desde;

    /** Derivado do snapshot na hora de serializar — não é persistido na presença. */
    private boolean controlador;

    public LiveMembroPresenca(Long idUsuario, String nome, String urlFotoPerfil, String instrumentos, Long desde) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.urlFotoPerfil = urlFotoPerfil;
        this.instrumentos = instrumentos;
        this.desde = desde;
    }
}
