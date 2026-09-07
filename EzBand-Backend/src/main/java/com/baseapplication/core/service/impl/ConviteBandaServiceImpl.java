package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.BandaDao;
import com.baseapplication.core.dao.ConviteExternoBandaDao;
import com.baseapplication.core.dao.EnsaioDao;
import com.baseapplication.core.dao.InstrumentoUsuarioDao;
import com.baseapplication.core.dao.MusicoBandaDao;
import com.baseapplication.core.dao.MusicoEventoDao;
import com.baseapplication.core.dao.NotificacaoDao;
import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.dto.ConviteExternoGeradoDTO;
import com.baseapplication.core.dto.ConviteExternoPreviewDTO;
import com.baseapplication.core.dto.EventoConviteDTO;
import com.baseapplication.core.dto.EventoPendenteConviteDTO;
import com.baseapplication.core.dto.GerarConviteExternoDTO;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.enums.SituacaoMusicoEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.exception.RestrictionException;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.ConviteExternoBanda;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.MusicoBandaId;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.MusicoEventoId;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.notificacao.ConviteParaUsuarioIngressarBanda;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.ConviteBandaService;
import com.baseapplication.core.service.MusicoBandaService;
import com.baseapplication.core.utils.Context;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Log4j2
public class ConviteBandaServiceImpl implements ConviteBandaService {

    private static final int DIAS_VALIDADE_CONVITE_EXTERNO = 30;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Autowired
    private ConviteExternoBandaDao conviteExternoBandaDao;

    @Autowired
    private NotificacaoDao notificacaoDao;

    @Autowired
    private BandaDao bandaDao;

    @Autowired
    private ShowDao showDao;

    @Autowired
    private EnsaioDao ensaioDao;

    @Autowired
    private MusicoEventoDao musicoEventoDao;

    @Autowired
    private MusicoBandaDao musicoBandaDao;

    @Autowired
    private MusicoBandaService musicoBandaService;

    @Autowired
    private InstrumentoUsuarioDao instrumentoUsuarioDao;

    @Override
    public List<EventoPendenteConviteDTO> buscarEventosPendentes(Long idBanda) {
        validarMembroDaBanda(idBanda, Context.getUsuarioLogado().getId());

        List<Evento> eventos = new ArrayList<>();
        eventos.addAll(showDao.buscarFuturosNaoRealizadosPorBanda(idBanda));
        eventos.addAll(ensaioDao.buscarFuturosNaoRealizadosPorBanda(idBanda));

        return eventos.stream()
                .sorted(Comparator.comparing(Evento::getData)
                        .thenComparing(Evento::getHorarioInicio, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(EventoPendenteConviteDTO::new)
                .toList();
    }

    @Override
    @Transactional
    public ConviteExternoGeradoDTO gerarConviteExterno(GerarConviteExternoDTO dto) {
        Usuario remetente = Context.getUsuarioLogado();
        Banda banda = buscarBanda(dto.getIdBanda());
        validarMembroDaBanda(banda.getId(), remetente.getId());

        ConviteExternoBanda convite = new ConviteExternoBanda();
        convite.setToken(UUID.randomUUID().toString());
        convite.setBanda(banda);
        convite.setIdUsuarioRemetente(remetente.getId());
        convite.setInstrumento(dto.getInstrumento());
        convite.setEventos(serializarEventos(banda.getId(), dto.getEventos()));
        convite.setDataExpiracao(LocalDateTime.now().plusDays(DIAS_VALIDADE_CONVITE_EXTERNO));
        conviteExternoBandaDao.save(convite);

        return new ConviteExternoGeradoDTO(convite.getToken(), montarLinkCadastro(convite.getToken()));
    }

    @Override
    public ConviteExternoPreviewDTO previewConviteExterno(String token) {
        Optional<ConviteExternoBanda> conviteOpt = conviteExternoBandaDao.findByToken(token);
        if (conviteOpt.isEmpty()) {
            return new ConviteExternoPreviewDTO(false, null, null, "Convite não encontrado.");
        }

        ConviteExternoBanda convite = conviteOpt.get();
        if (convite.isUtilizado()) {
            return new ConviteExternoPreviewDTO(false, convite.getBanda().getNome(),
                    convite.getBanda().getUrlLogo(), "Este convite já foi utilizado.");
        }
        if (convite.isExpirado()) {
            return new ConviteExternoPreviewDTO(false, convite.getBanda().getNome(),
                    convite.getBanda().getUrlLogo(), "Este convite expirou.");
        }
        return new ConviteExternoPreviewDTO(true, convite.getBanda().getNome(),
                convite.getBanda().getUrlLogo(), null);
    }

    @Override
    @Transactional
    public Long aceitarConvitePorToken(String token) {
        Usuario usuarioLogado = Context.getUsuarioLogado();

        Optional<ConviteParaUsuarioIngressarBanda> notificacaoOpt = notificacaoDao.findConviteByLinkToken(token);
        if (notificacaoOpt.isPresent()) {
            return aceitarConviteDeNotificacao(notificacaoOpt.get(), usuarioLogado);
        }

        ConviteExternoBanda conviteExterno = conviteExternoBandaDao.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Convite não encontrado ou expirado."));
        return aceitarConviteExterno(conviteExterno, usuarioLogado);
    }

    private Long aceitarConviteDeNotificacao(ConviteParaUsuarioIngressarBanda convite, Usuario usuarioLogado) {
        if (!convite.getDestinatarioId().equals(usuarioLogado.getId())) {
            throw new RestrictionException("Este convite não é para você.");
        }

        Banda banda = buscarBanda(convite.getRemetenteId());
        ingressarNaBanda(usuarioLogado, banda, convite.getInstrumento());
        incluirUsuarioNosEventos(usuarioLogado, banda, convite.getEventos());

        convite.setLida(true);
        notificacaoDao.save(convite);
        return banda.getId();
    }

    private Long aceitarConviteExterno(ConviteExternoBanda convite, Usuario usuarioLogado) {
        if (convite.isUtilizado()) {
            throw new RestrictionException("Este convite já foi utilizado.");
        }
        if (convite.isExpirado()) {
            throw new RestrictionException("Este convite expirou.");
        }

        Banda banda = convite.getBanda();
        ingressarNaBanda(usuarioLogado, banda, convite.getInstrumento());
        incluirUsuarioNosEventos(usuarioLogado, banda, convite.getEventos());

        convite.setIdUsuarioAceitou(usuarioLogado.getId());
        convite.setDataAceite(LocalDateTime.now());
        conviteExternoBandaDao.save(convite);
        return banda.getId();
    }

    private void ingressarNaBanda(Usuario usuario, Banda banda, String instrumento) {
        if (musicoBandaDao.existsById(new MusicoBandaId(usuario.getId(), banda.getId()))) {
            return;
        }
        String instrumentos = (instrumento == null || instrumento.isBlank())
                ? instrumentoPreferido(usuario.getId())
                : instrumento;
        musicoBandaService.cadastrarUsuarioEmBanda(usuario, banda, instrumentos,
                List.of(PermissaoMusico.MEMBRO_REGULAR));
    }

    @Override
    public String serializarEventos(Long idBanda, List<EventoConviteDTO> eventos) {
        if (eventos == null || eventos.isEmpty()) {
            return null;
        }

        Set<String> chaves = new LinkedHashSet<>();
        for (EventoConviteDTO evento : eventos) {
            if (evento == null || evento.getId() == null || evento.getTipoEvento() == null) {
                continue;
            }
            if (buscarEventoDaBanda(idBanda, evento.getId(), evento.getTipoEvento()) != null) {
                chaves.add(evento.getTipoEvento().name() + ":" + evento.getId());
            }
        }
        return chaves.isEmpty() ? null : String.join(",", chaves);
    }

    @Override
    @Transactional
    public void incluirUsuarioNosEventos(Usuario usuario, Banda banda, String eventosSerializados) {
        if (eventosSerializados == null || eventosSerializados.isBlank()) {
            return;
        }

        String instrumentos = instrumentoNaBanda(usuario.getId(), banda.getId());

        for (String chave : eventosSerializados.split(",")) {
            String[] partes = chave.trim().split(":");
            if (partes.length != 2) {
                continue;
            }
            try {
                TipoEvento tipoEvento = TipoEvento.valueOf(partes[0]);
                Long idEvento = Long.valueOf(partes[1]);

                Evento evento = buscarEventoDaBanda(banda.getId(), idEvento, tipoEvento);
                if (evento == null) {
                    continue;
                }

                MusicoEventoId id = new MusicoEventoId(idEvento, usuario.getId(), tipoEvento);
                if (musicoEventoDao.existsById(id)) {
                    continue;
                }

                MusicoEvento musicoEvento = new MusicoEvento();
                musicoEvento.setId(id);
                musicoEvento.setUsuario(usuario);
                musicoEvento.setEvento(evento);
                musicoEvento.setInstrumentos(instrumentos);
                musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
                musicoEventoDao.save(musicoEvento);
            } catch (IllegalArgumentException e) {
                log.warn("Chave de evento invalida no convite: {}", chave);
            }
        }
    }

    @Override
    @Transactional
    public boolean atualizarConvitePendente(Long idBanda, Long idUsuarioConvidado, String eventosSerializados) {
        List<ConviteParaUsuarioIngressarBanda> pendentes =
                notificacaoDao.buscarConvitesBandaNaoLidos(idBanda, idUsuarioConvidado);
        if (pendentes.isEmpty()) {
            return false;
        }
        if (eventosSerializados != null && !eventosSerializados.isBlank()) {
            for (ConviteParaUsuarioIngressarBanda convite : pendentes) {
                convite.setEventos(mesclarEventos(convite.getEventos(), eventosSerializados));
                notificacaoDao.save(convite);
            }
        }
        return true;
    }

    private String mesclarEventos(String atuais, String novos) {
        Set<String> chaves = new LinkedHashSet<>();
        if (atuais != null && !atuais.isBlank()) {
            for (String chave : atuais.split(",")) {
                chaves.add(chave.trim());
            }
        }
        for (String chave : novos.split(",")) {
            chaves.add(chave.trim());
        }
        return String.join(",", chaves);
    }

    private Evento buscarEventoDaBanda(Long idBanda, Long idEvento, TipoEvento tipoEvento) {
        Evento evento = switch (tipoEvento) {
            case SHOW -> showDao.findById(idEvento).orElse(null);
            case ENSAIO -> ensaioDao.findById(idEvento).orElse(null);
        };
        if (evento == null || evento.getBanda() == null || !evento.getBanda().getId().equals(idBanda)) {
            return null;
        }
        return evento;
    }

    private String instrumentoNaBanda(Long idUsuario, Long idBanda) {
        MusicoBanda musicoBanda = musicoBandaDao.findById(new MusicoBandaId(idUsuario, idBanda)).orElse(null);
        if (musicoBanda != null && musicoBanda.getInstrumentos() != null && !musicoBanda.getInstrumentos().isBlank()) {
            return musicoBanda.getInstrumentos();
        }
        return instrumentoPreferido(idUsuario);
    }

    private String instrumentoPreferido(Long idUsuario) {
        return instrumentoUsuarioDao.findByIdUsuarioOrderByFavoritoDescNomeAsc(idUsuario).stream()
                .findFirst()
                .map(instrumento -> instrumento.getNome())
                .orElse("");
    }

    private Banda buscarBanda(Long idBanda) {
        return bandaDao.findById(idBanda)
                .orElseThrow(() -> new ResourceNotFoundException("Banda não encontrada. Ela pode ter sido deletada."));
    }

    private void validarMembroDaBanda(Long idBanda, Long idUsuario) {
        if (!musicoBandaDao.existsById(new MusicoBandaId(idUsuario, idBanda))) {
            throw new RestrictionException("Você não faz parte desta banda.");
        }
    }

    private String montarLinkCadastro(String token) {
        return frontendUrl + "/cadastro?convite=" + token;
    }
}
