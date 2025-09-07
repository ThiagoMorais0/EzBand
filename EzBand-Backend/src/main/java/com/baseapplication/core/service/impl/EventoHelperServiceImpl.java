package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.NotificacaoDao;
import com.baseapplication.core.dao.RespostaNotificacaoDao;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.enums.SituacaoMusicoEvento;
import com.baseapplication.core.enums.AcaoResposta;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.model.notificacao.ConviteParaEvento;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.*;
import jakarta.persistence.EntityManager;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoHelperServiceImpl implements EventoHelperService {

    @Autowired
    private ShowService showService;

    @Autowired
    private EnsaioService ensaioService;

    @Autowired
    private NotificacaoDao notificacaoDao;

    @Autowired
    private RespostaNotificacaoDao respostaNotificacaoDao;

    @Autowired
    private MusicoEventoService musicoEventoService;

    @Override
    public Evento buscarEvento(Long idEvento, TipoEvento tipoEvento) {
        switch (tipoEvento){
            case SHOW -> {return showService.buscarPorId(idEvento);}
            case ENSAIO -> {return ensaioService.buscarPorId(idEvento);}
            default -> {throw new ServiceException("Evento não encontrado");}
        }
    }

    @Override
    public EventosSeparadosDTO buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
        return new EventosSeparadosDTO(
                showService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario)
                        .stream().map(ShowDTO::new).collect(Collectors.toList()),
                ensaioService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario)
                        .stream().map(EnsaioDTO::new).collect(Collectors.toList())
        );
    }

    @Override
    public List<Show> buscarShowsPendentesPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
        return showService.buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, StatusEvento.PENDENTE.toString());
    }

    @Override
    public List<Show> buscarShowsAguardandoPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
        return showService.buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, StatusEvento.AGUARDANDO_APROVACAO.toString());
    }

    @Override
    public List<Ensaio> buscarEnsaiosPendentesPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
        return ensaioService.buscarEnsaiosPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, StatusEvento.PENDENTE.toString());
    }

    @Override
    public void alterarStatus(Long idEvento, TipoEvento tipoEvento, StatusEvento novoStatus) {
        switch (tipoEvento){
            case ENSAIO -> ensaioService.alterarStatus(idEvento, novoStatus);
            case SHOW -> showService.alterarStatus(idEvento, novoStatus);
        }
    }

    @Override
    public void adicionarMusicoAoEventoEVerificarSeTodosConvitesForamAceitos(Long idEvento, TipoEvento tipoEvento, Long idUsuarioConvidado) {
        switch (tipoEvento){
            case ENSAIO -> ensaioService.adicionarMusicoAoEnsaio(idEvento, idUsuarioConvidado);
            case SHOW -> showService.adicionarMusicoAoShow(idEvento, idUsuarioConvidado);
        }
        // Verificar se todos convites foram aceitos, se foram, alterar status do evento para PENDENTE
        List<ConviteParaEvento> convites = notificacaoDao.buscarConvitesParaEvento(idEvento, tipoEvento);
        if(convites == null || convites.isEmpty()){
            return;
        }
        List<Long> idsNotificacoes = convites.stream().map(Notificacao::getId).toList();
        long totalAceitos = respostaNotificacaoDao.countByNotificacaoIdInAndAcao(idsNotificacoes, AcaoResposta.ACEITAR);
        if(totalAceitos == convites.size()){
            alterarStatus(idEvento, tipoEvento, StatusEvento.PENDENTE);
        }

    }

    @Override
    public void registrarRespostaConviteEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuarioConvidado, AcaoResposta acao) {
        // Atualiza situação do músico no evento com base na ação
        MusicoEvento musico = musicoEventoService.buscar(idEvento, tipoEvento, idUsuarioConvidado);
        if (acao == AcaoResposta.ACEITAR) {
            musico.setSituacao(SituacaoMusicoEvento.ATIVO);
        } else if (acao == AcaoResposta.RECUSAR) {
            musico.setSituacao(SituacaoMusicoEvento.RECUSADO);
        }
        musicoEventoService.salvar(musico);

        // Reavalia o status do evento
        List<MusicoEvento> musicos = musicoEventoService.listarPorEvento(idEvento, tipoEvento);

        // Se existir algum recusado, cancelar o evento
        boolean existeRecusado = musicos.stream().anyMatch(m -> SituacaoMusicoEvento.RECUSADO.equals(m.getSituacao()));
        if (existeRecusado) {
            alterarStatus(idEvento, tipoEvento, StatusEvento.CANCELADO);
            return;
        }

        // Se não há mais convites pendentes, todos estão ativos/inativos -> evento pendente
        boolean existePendente = musicos.stream().anyMatch(m -> SituacaoMusicoEvento.CONVITE_PENDENTE.equals(m.getSituacao()));
        if (!existePendente) {
            alterarStatus(idEvento, tipoEvento, StatusEvento.PENDENTE);
        }
    }

    @Override
    public List<Ensaio> buscarEnsaiosAguardandoPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
        return ensaioService.buscarEnsaiosPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, StatusEvento.AGUARDANDO_APROVACAO.toString());
    }
}
