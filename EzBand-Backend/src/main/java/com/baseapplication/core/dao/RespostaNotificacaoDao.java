package com.baseapplication.core.dao;

import com.baseapplication.core.model.notificacao.RespostaNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.baseapplication.core.enums.AcaoResposta;
import java.util.List;

@Repository
public interface RespostaNotificacaoDao extends JpaRepository<RespostaNotificacao, Long> {
    long countByNotificacaoIdInAndAcao(List<Long> notificacaoIds, AcaoResposta acao);
}
