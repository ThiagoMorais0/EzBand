package com.baseapplication.core.dao;

import com.baseapplication.core.model.notificacao.RespostaNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespostaNotificacaoDao extends JpaRepository<RespostaNotificacao, Long> {
}
