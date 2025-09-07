package com.baseapplication.core.dao;

import java.util.List;

import com.baseapplication.core.enums.TipoParticipante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.enums.StatusNotificacao;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.notificacao.ConviteParaEvento;
import com.baseapplication.core.model.superClasses.Notificacao;

@Repository
public interface NotificacaoDao extends JpaRepository<Notificacao, Long> {

    @Query("SELECT n FROM Notificacao n WHERE n.destinatarioId = :destinatarioId AND n.destinatarioTipo = :destinatarioTipo")
    List<Notificacao> buscarNaoLidas(@Param("destinatarioId") Long destinatarioId,
                                     @Param("destinatarioTipo") TipoParticipante destinatarioTipo);

    @Query("SELECT c FROM ConviteParaEvento c WHERE c.idEventoConvite = :idEvento AND c.tipoEventoConvite = :tipoEvento")
    List<ConviteParaEvento> buscarConvitesParaEvento(@Param("idEvento") Long idEvento,
                                                     @Param("tipoEvento") TipoEvento tipoEvento);
}
