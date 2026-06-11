package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.ConfiguracaoNotificacaoUsuarioDao;
import com.baseapplication.core.dto.ConfiguracaoNotificacaoUsuarioDTO;
import com.baseapplication.core.model.ConfiguracaoNotificacaoUsuario;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.ConfiguracaoNotificacaoUsuarioService;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfiguracaoNotificacaoUsuarioServiceImpl implements ConfiguracaoNotificacaoUsuarioService {

    private final ConfiguracaoNotificacaoUsuarioDao dao;

    @Override
    @Transactional(readOnly = true)
    public ConfiguracaoNotificacaoUsuarioDTO buscarParaUsuarioLogado() {
        Usuario usuario = Context.getUsuarioLogado();
        return dao.findByIdUsuario(usuario.getId())
                .map(ConfiguracaoNotificacaoUsuarioDTO::new)
                .orElseGet(() -> {
                    ConfiguracaoNotificacaoUsuarioDTO dto = new ConfiguracaoNotificacaoUsuarioDTO();
                    dto.setIdUsuario(usuario.getId());
                    return dto;
                });
    }

    @Override
    @Transactional
    public ConfiguracaoNotificacaoUsuarioDTO salvarOuAtualizar(ConfiguracaoNotificacaoUsuarioDTO dto) {
        Usuario usuario = Context.getUsuarioLogado();
        dto.setIdUsuario(usuario.getId());

        ConfiguracaoNotificacaoUsuario entity = dao.findByIdUsuario(usuario.getId())
                .orElse(new ConfiguracaoNotificacaoUsuario());

        entity.setIdUsuario(usuario.getId());
        entity.setReceberNotificacoesWhatsapp(dto.getReceberNotificacoesWhatsapp());
        entity.setDiasAntecedenciaLembrete(dto.getDiasAntecedenciaLembrete());
        entity.setDiaSemanaResumoSemanal(dto.getDiaSemanaResumoSemanal());

        ConfiguracaoNotificacaoUsuario salvo = dao.save(entity);
        log.info("Configuração de notificação salva para usuário {}", usuario.getId());
        return new ConfiguracaoNotificacaoUsuarioDTO(salvo);
    }
}
