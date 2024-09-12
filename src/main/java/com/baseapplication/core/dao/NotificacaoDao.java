package com.baseapplication.core.dao;

import com.baseapplication.core.model.superClasses.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacaoDao extends JpaRepository<Notificacao, Long> {
}
