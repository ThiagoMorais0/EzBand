package com.baseapplication.core.dao;

import com.baseapplication.core.model.EstadoNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EstadoNotificacaoDao extends JpaRepository<EstadoNotificacao, Long> {

}
