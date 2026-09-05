package com.baseapplication.core.service;

import com.baseapplication.core.enums.TipoEvento;

public interface NotificacaoEventoService {
    
    void notificarNovoEvento(Long idEvento, TipoEvento tipoEvento);

    void notificarNovoEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuarioCriador);

    void verificarENotificarEventosProximos();

    /**
     * Avisa todos os músicos vinculados ao evento (banda ou avulsos) que o Modo Palco começou —
     * exceto quem o abriu. Só deve ser chamado uma vez por sessão, na abertura.
     */
    void notificarSessaoPalcoIniciada(Long idEvento, TipoEvento tipoEvento, Long idUsuarioIniciador, String nomeIniciador);
}
