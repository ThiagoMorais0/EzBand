package com.baseapplication.core.live;

import com.baseapplication.core.dto.live.LiveMembroPresenca;
import com.baseapplication.core.enums.TipoEvento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Tudo que o handshake resolveu sobre quem está do outro lado do socket.
 *
 * <p>Fica num único atributo da {@code WebSocketSession} ({@link #ATTR}) em vez de meia
 * dúzia de chaves soltas de String — o handler nunca precisa lembrar nome de chave nem
 * fazer cast.
 */
@Getter
@Setter
@NoArgsConstructor
public class LiveUsuarioSessao implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ATTR = "liveUsuarioSessao";

    private Long idUsuario;
    private String nome;
    private String urlFotoPerfil;
    private String instrumentos;

    private Long idEvento;
    private TipoEvento tipoEvento;
    private Long idBanda;

    private String roomKey;
    private long conectadoEm;

    /**
     * Quem abriu a sessão pediu para avisar o resto da banda?
     *
     * <p>Vem do handshake e só importa para a conexão que cria a sessão. O padrão é
     * verdadeiro: cliente antigo que não manda nada continua notificando como sempre.
     */
    private boolean notificarMembros = true;

    public LiveMembroPresenca toPresenca() {
        return new LiveMembroPresenca(idUsuario, nome, urlFotoPerfil, instrumentos, conectadoEm);
    }

    /** Uma sala por evento: {@code SHOW:123}, {@code ENSAIO:456}. */
    public static String roomKey(TipoEvento tipoEvento, Long idEvento) {
        return tipoEvento.name() + ":" + idEvento;
    }
}
