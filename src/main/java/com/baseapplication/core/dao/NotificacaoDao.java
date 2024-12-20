package com.baseapplication.core.dao;

import com.baseapplication.core.enums.StatusNotificacao;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoDao extends JpaRepository<Notificacao, Long> {
    @Query("SELECT n FROM Notificacao n " +
            "WHERE n.destinatario.id = :idUsuario and n.statusNotificacao = :status")
    List<Notificacao> buscarNotificacoesPorUsuario(Long idUsuario, StatusNotificacao status);

    @Query("SELECT n FROM Notificacao n " +
            "INNER JOIN NotificacaoShow ns on ns.show.id = :idShow")
    List<Notificacao> buscarNotificacoesShowMembros(Long idShow);

    @Query("SELECT n FROM Notificacao n " +
            "INNER JOIN NotificacaoEnsaio ne on ne.ensaio.id = :idEnsaio")
    List<Notificacao> buscarNotificacoesEnsaioMembros(Long idEnsaio);
}
