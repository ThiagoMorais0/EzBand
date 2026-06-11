package com.baseapplication.core.dao;

import com.baseapplication.core.model.ConfiguracaoNotificacaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracaoNotificacaoUsuarioDao extends JpaRepository<ConfiguracaoNotificacaoUsuario, Long> {

    Optional<ConfiguracaoNotificacaoUsuario> findByIdUsuario(Long idUsuario);

    List<ConfiguracaoNotificacaoUsuario> findByReceberNotificacoesWhatsappTrue();
}
