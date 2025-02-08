package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.dto.superClasses.NotificacaoDTO;
import com.baseapplication.core.service.*;
import com.baseapplication.core.utils.Context;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    private EventoHelperService eventoHelperService;

    @Autowired
    private ImagemService imagemService;

    @Autowired
    private ReporteDeErroService reporteDeErroService;

    @Autowired
    private EmailService emailService;

//    @Override
//    public Usuario findByLogin(String login) {
//        return usuarioDao.findByUsername(login);
//    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioDao.findByEmail(email);
    }

    @Override
    public void salvar(Usuario usuario) {
        usuarioDao.save(usuario);
    }

    @Override
    public InfoUsuarioPainelDTO buscarInfoPainel() {
        try {
            Long idUsuario = Context.getUsuarioLogado().getId();
            CompletableFuture<List<BandaDTO>> bandasFuture = buscarBandasDoUsuarioAsync(idUsuario);
            CompletableFuture<Integer> notificacoesFuture = buscarQuantidadeNotificacoesAsync(idUsuario);
            CompletableFuture<Integer> participacoesEspeciaisFuture = buscarQuantidadeParticipacoesEspeciaisAsync(idUsuario);
            CompletableFuture<Integer> proximosEventosFuture = buscarQuantidadeProximosEventosAsync(idUsuario);

            CompletableFuture.allOf(bandasFuture, notificacoesFuture, participacoesEspeciaisFuture, proximosEventosFuture).join();

            return new InfoUsuarioPainelDTO(
                    bandasFuture.get(),
                    notificacoesFuture.get(),
                    participacoesEspeciaisFuture.get(),
                    proximosEventosFuture.get());

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

    }

    private CompletableFuture<Integer> buscarQuantidadeProximosEventosAsync(Long idUsuario) {
        return CompletableFuture.supplyAsync(() -> buscarQuantidadeProximosEventos(idUsuario));
    }


    private CompletableFuture<Integer> buscarQuantidadeParticipacoesEspeciaisAsync(Long idUsuario) {
        return CompletableFuture.supplyAsync(() -> buscarQuantidadeParticipacoesEspeciais(idUsuario));
    }

    private Integer buscarQuantidadeParticipacoesEspeciais(Long idUsuario) {
        return usuarioDao.buscarQuantidadeParticipacoesEspeciais(idUsuario);
    }

    private CompletableFuture<Integer> buscarQuantidadeNotificacoesAsync(Long idUsuario) {
        return CompletableFuture.supplyAsync(() -> buscarQuantidadeNotificacoes(idUsuario));
    }

    private Integer buscarQuantidadeProximosEventos(Long idUsuario) {
        return usuarioDao.buscarQuantidadeProximosEventos(idUsuario);
    }

    @Override
    public void atualizarInformacoesPerfil(InfoPerfilUsuarioDTO infoPerfil) {
        Usuario usuario = usuarioDao.findByEmail(infoPerfil.getEmail());
        BeanUtils.copyProperties(infoPerfil, usuario);
        usuarioDao.save(usuario);
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioDao.findById(id).get();
    }

    @Override
    public Usuario findByCelular(String celular) {
        return usuarioDao.findByCelular(celular);
    }

    @Override
    public Usuario buscarPorContato(String contato, TipoContato tipoContato) {
        switch (tipoContato) {
            case EMAIL -> {
                return findByEmail(contato);
            }
            case CELULAR -> {
                return findByCelular(contato);
            }
            default -> {
                throw new ServiceException("Tipo de contato inválido");
            }
        }
    }

    @Override
    public Boolean verificarEmailJaCadastrado(String email) {
        return usuarioDao.findByEmail(email) != null;
    }

    @Override
    public InfoPerfilUsuarioDTO editarUsuarioComImagem(InfoPerfilUsuarioDTO usuarioDTO, MultipartFile imagem) {
        String urlImagem = imagemService.saveImageAndGetUrl(imagem);
        Usuario usuario = buscarPorContato(usuarioDTO.getEmail(), TipoContato.EMAIL);
        BeanUtils.copyProperties(usuarioDTO, usuario);
        usuario.setUrlFotoPerfil(urlImagem);
        usuario.setAtivo(true);
        usuarioDao.save(usuario);
        usuarioDTO.setUrlFotoPerfil(urlImagem);
        return usuarioDTO;
    }

    @Override
    public Integer buscarQuantidadeNotificacoes(Long idUsuario) {
        return usuarioDao.buscarQuantidadeNotificacoes(idUsuario);
    }

    @Override
    public void enviarSolicitacaoParaIngressarBanda(Long idBanda, Long idUsuarioRemetente, String instrumento) {
        notificacaoService.enviarSolicitacaoParaIngressarBanda(
                bandaService.buscarPorId(idBanda),
                buscarPorId(idUsuarioRemetente),
                instrumento);
    }

    @Override
    public InfoPerfilUsuarioDTO buscarInformacoesDoPerfilPorId(Long idUsuario) {
        return new InfoPerfilUsuarioDTO(buscarPorId(idUsuario));
    }

    @Override
    public List<NotificacaoDTO> buscarNotificacoesUsuario(Long idUsuario) {
        return notificacaoService.buscarNotificacoesPorUsuario(idUsuario).stream().map(i -> new NotificacaoDTO().toDTO(i)).collect(Collectors.toList());
    }

    @Override
    public void reportarErro(String mensagem) {
        reporteDeErroService.reportarErro(mensagem, Context.getUsuarioLogado());
    }

    @Override
    public void enviarEmail(EmailDTO email) {
        emailService.enviarEmail(
                email.getAssunto(),
                email.getMensagem(),
                email.getDestinatario()
        );
    }

    private CompletableFuture<EventosSeparadosDTO> buscarProximosEventosAsync(Long idUsuario) {
        return CompletableFuture.supplyAsync(() ->
                eventoHelperService.buscarPendentesPorUsuarioOrdenadoPorData(idUsuario));
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
                bandaService.buscarBandasPorUsuario(idUsuario)
                        .stream().map(BandaDTO::new).toList());
    }
}
