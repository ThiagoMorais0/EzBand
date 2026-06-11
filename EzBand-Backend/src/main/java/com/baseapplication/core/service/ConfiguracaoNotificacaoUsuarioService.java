package com.baseapplication.core.service;

import com.baseapplication.core.dto.ConfiguracaoNotificacaoUsuarioDTO;

public interface ConfiguracaoNotificacaoUsuarioService {

    ConfiguracaoNotificacaoUsuarioDTO buscarParaUsuarioLogado();

    ConfiguracaoNotificacaoUsuarioDTO salvarOuAtualizar(ConfiguracaoNotificacaoUsuarioDTO dto);
}
