package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.BandaParticipacaoEspecialDTO;
import com.baseapplication.core.dto.EventosSeparadosDTO;
import com.baseapplication.core.dto.InfoUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioPainelDTO;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.dto.superClasses.NotificacaoDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.EventoService;
import com.baseapplication.core.service.NotificacaoService;
import com.baseapplication.core.service.UsuarioService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private BandaService bandaService;

    @Autowired
    private NotificacaoService notificacaoService;

    @Autowired
    private EventoService eventoService;

    @Override
    public Usuario findByLogin(String login) {
        return usuarioDao.findByUsername(login);
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioDao.findByEmail(email);
    }

    @Override
    public void salvar(Usuario usuario) {
        usuarioDao.save(usuario);
    }

    @Override
    public InfoUsuarioPainelDTO buscarInfoPainel(Long idUsuario) {
        try{
            CompletableFuture<List<BandaDTO>> bandasFuture = buscarBandasDoUsuarioAsync(idUsuario);
            CompletableFuture<List<NotificacaoDTO>> notificacoesFuture = buscarNotificacoesAsync(idUsuario);
            CompletableFuture<List<BandaParticipacaoEspecialDTO>> participacoesEspeciaisFuture = buscarParticipacoesEspeciaisAsync(idUsuario);
            CompletableFuture<EventosSeparadosDTO> proximosEventosFuture = buscarProximosEventosAsync(idUsuario);

            CompletableFuture.allOf(bandasFuture, notificacoesFuture, participacoesEspeciaisFuture, proximosEventosFuture).join();

            return new InfoUsuarioPainelDTO(
                    bandasFuture.get(),
                    notificacoesFuture.get(),
                    participacoesEspeciaisFuture.get(),
                    proximosEventosFuture.get());

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e){
            throw new ServiceException(e.getMessage());
        }

    }

    private CompletableFuture<EventosSeparadosDTO> buscarProximosEventosAsync(Long idUsuario) {
        return CompletableFuture.supplyAsync(() ->
                eventoService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario));
    }

    private CompletableFuture<List<BandaParticipacaoEspecialDTO>> buscarParticipacoesEspeciaisAsync(Long idUsuario) {
        return CompletableFuture.supplyAsync(() ->
                bandaService.buscarParticipacoesEspeciais(idUsuario).stream().map(BandaParticipacaoEspecialDTO::new).toList());
    }

    private CompletableFuture<List<NotificacaoDTO>> buscarNotificacoesAsync(Long idUsuario) {
        return CompletableFuture.supplyAsync(() ->
                notificacaoService.buscarNotificacoesPorUsuario(idUsuario).stream()
                        .map(new NotificacaoDTO()::toDTO).toList());
    }

    private CompletableFuture<List<BandaDTO>> buscarBandasDoUsuarioAsync(Long idUsuario) {
        return CompletableFuture.supplyAsync(() ->
                bandaService.buscarBandasPorUsuario(idUsuario).stream().map(BandaDTO::new).toList());
    }
}
