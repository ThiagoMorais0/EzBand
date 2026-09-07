package com.baseapplication.core.dao;

import java.util.List;
import java.util.Optional;

import com.baseapplication.core.enums.TipoParticipante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.baseapplication.core.enums.StatusNotificacao;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.notificacao.ConviteParaEvento;
import com.baseapplication.core.model.notificacao.ConviteParaUsuarioIngressarBanda;
import com.baseapplication.core.model.superClasses.Notificacao;

@Repository
public interface NotificacaoDao extends JpaRepository<Notificacao, Long> {

    @Query("SELECT n FROM Notificacao n WHERE n.destinatarioId = :destinatarioId AND n.destinatarioTipo = :destinatarioTipo AND n.lida = false ORDER BY n.dataCriacao DESC")
    List<Notificacao> buscarNaoLidas(@Param("destinatarioId") Long destinatarioId,
                                     @Param("destinatarioTipo") TipoParticipante destinatarioTipo);

    @Query("SELECT c FROM ConviteParaEvento c WHERE c.idEventoConvite = :idEvento AND c.tipoEventoConvite = :tipoEvento")
    List<ConviteParaEvento> buscarConvitesParaEvento(@Param("idEvento") Long idEvento,
                                                     @Param("tipoEvento") TipoEvento tipoEvento);

    @Query("SELECT n FROM Notificacao n WHERE n.destinatarioId = :destinatarioId AND n.destinatarioTipo = :destinatarioTipo AND n.remetenteId = :remetenteId AND n.remetenteTipo = :remetenteTipo AND TYPE(n) = :tipoNotificacao AND n.lida = false")
    List<Notificacao> buscarNotificacoesSimilaresNaoLidas(
            @Param("destinatarioId") Long destinatarioId,
            @Param("destinatarioTipo") TipoParticipante destinatarioTipo,
            @Param("remetenteId") Long remetenteId,
            @Param("remetenteTipo") TipoParticipante remetenteTipo,
            @Param("tipoNotificacao") Class<? extends Notificacao> tipoNotificacao
    );

    @Query("SELECT c FROM ConviteParaUsuarioIngressarBanda c WHERE c.linkToken = :token")
    Optional<ConviteParaUsuarioIngressarBanda> findConviteByLinkToken(@Param("token") String token);

    @Query("SELECT c FROM ConviteParaUsuarioIngressarBanda c WHERE c.destinatarioId = :idUsuario " +
            "AND c.remetenteId = :idBanda AND c.lida = false")
    List<ConviteParaUsuarioIngressarBanda> buscarConvitesBandaNaoLidos(@Param("idBanda") Long idBanda,
                                                                      @Param("idUsuario") Long idUsuario);
}
