package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.BandaDao;
import com.baseapplication.core.dao.ConviteExternoBandaDao;
import com.baseapplication.core.dao.EnsaioDao;
import com.baseapplication.core.dao.InstrumentoUsuarioDao;
import com.baseapplication.core.dao.MusicoBandaDao;
import com.baseapplication.core.dao.MusicoEventoDao;
import com.baseapplication.core.dao.NotificacaoDao;
import com.baseapplication.core.dao.RespostaNotificacaoDao;
import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.ConviteAceitoDTO;
import com.baseapplication.core.dto.ConviteExternoGeradoDTO;
import com.baseapplication.core.dto.ConviteExternoPreviewDTO;
import com.baseapplication.core.dto.EventoConviteDTO;
import com.baseapplication.core.dto.EventoPendenteConviteDTO;
import com.baseapplication.core.dto.GerarConviteExternoDTO;
import com.baseapplication.core.enums.AcaoResposta;
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
import com.baseapplication.core.model.notificacao.RespostaNotificacao;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.ConviteBandaService;
import com.baseapplication.core.service.MusicoBandaService;
import com.baseapplication.core.utils.Context;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

    /** Sem 0/O/1/I/L: o código é lido em voz alta e digitado à mão. */
    private static final String ALFABETO_CODIGO = "23456789ABCDEFGHJKMNPQRSTUVWXYZ";
    /** 8 caracteres (31^8 ≈ 8,5·10^11) para que adivinhar um convite válido não seja viável. */
    private static final int TAMANHO_CODIGO = 8;
    private static final int TENTATIVAS_CODIGO_UNICO = 10;

    private static final SecureRandom RANDOM = new SecureRandom();

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

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private RespostaNotificacaoDao respostaNotificacaoDao;

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
        convite.setCodigo(gerarCodigoUnico());
        convite.setBanda(banda);
        convite.setIdUsuarioRemetente(remetente.getId());
        convite.setInstrumento(dto.getInstrumento());
        convite.setEventos(serializarEventos(banda.getId(), dto.getEventos()));
        convite.setDataExpiracao(LocalDateTime.now().plusDays(DIAS_VALIDADE_CONVITE_EXTERNO));
        conviteExternoBandaDao.save(convite);

        return new ConviteExternoGeradoDTO(
                convite.getToken(),
                montarLinkConvite(convite.getToken()),
                formatarCodigo(convite.getCodigo()));
    }

    @Override
    public ConviteExternoPreviewDTO previewConviteExterno(String token) {
        Optional<ConviteExternoBanda> externoOpt = buscarConviteExterno(token);
        if (externoOpt.isPresent()) {
            return previewDeConviteExterno(externoOpt.get());
        }

        Optional<ConviteParaUsuarioIngressarBanda> notificacaoOpt = notificacaoDao.findConviteByLinkToken(token);
        if (notificacaoOpt.isPresent()) {
            return previewDeNotificacao(notificacaoOpt.get());
        }

        return ConviteExternoPreviewDTO.invalido("Convite não encontrado.");
    }

    private ConviteExternoPreviewDTO previewDeConviteExterno(ConviteExternoBanda convite) {
        Banda banda = convite.getBanda();
        ConviteExternoPreviewDTO dto = montarPreview(banda, convite.getInstrumento(), convite.getEventos());
        dto.setNomeRemetente(nomeUsuario(convite.getIdUsuarioRemetente()));

        if (convite.isUtilizado()) {
            // Quem reabre o proprio link ja usado nao pode ver isso como erro: o destino dele e a banda.
            dto.setValido(dto.isJaEMembro());
            dto.setMotivo(dto.isJaEMembro() ? null : "Este convite já foi utilizado.");
            return dto;
        }
        if (convite.isExpirado()) {
            dto.setValido(false);
            dto.setMotivo("Este convite expirou.");
            return dto;
        }
        dto.setValido(true);
        return dto;
    }

    private ConviteExternoPreviewDTO previewDeNotificacao(ConviteParaUsuarioIngressarBanda convite) {
        Banda banda = bandaDao.findById(convite.getRemetenteId()).orElse(null);
        if (banda == null) {
            return ConviteExternoPreviewDTO.invalido("A banda deste convite não existe mais.");
        }

        ConviteExternoPreviewDTO dto = montarPreview(banda, convite.getInstrumento(), convite.getEventos());
        Long idLogado = idUsuarioLogadoOuNull();

        if (idLogado != null && !idLogado.equals(convite.getDestinatarioId())) {
            dto.setValido(false);
            dto.setMotivo("Este convite é de outra pessoa.");
            return dto;
        }
        if (jaRespondido(convite.getId()) && !dto.isJaEMembro()) {
            dto.setValido(false);
            dto.setMotivo("Este convite já foi respondido.");
            return dto;
        }
        dto.setValido(true);
        return dto;
    }

    private ConviteExternoPreviewDTO montarPreview(Banda banda, String instrumento, String eventos) {
        ConviteExternoPreviewDTO dto = new ConviteExternoPreviewDTO();
        dto.setIdBanda(banda.getId());
        dto.setNomeBanda(banda.getNome());
        dto.setUrlLogo(banda.getUrlLogo());
        dto.setInstrumento(instrumento);
        dto.setQuantidadeEventos(contarEventos(eventos));

        Long idLogado = idUsuarioLogadoOuNull();
        dto.setJaEMembro(idLogado != null && ehMembro(idLogado, banda.getId()));
        return dto;
    }

    private int contarEventos(String eventosSerializados) {
        if (eventosSerializados == null || eventosSerializados.isBlank()) {
            return 0;
        }
        return (int) Arrays.stream(eventosSerializados.split(","))
                .filter(chave -> !chave.isBlank())
                .count();
    }

    private String nomeUsuario(Long idUsuario) {
        if (idUsuario == null) {
            return null;
        }
        return usuarioDao.findById(idUsuario).map(Usuario::getNome).orElse(null);
    }

    /** O preview e publico: sem sessao o Context devolve um Usuario vazio, sem id. */
    private Long idUsuarioLogadoOuNull() {
        Usuario usuario = Context.getUsuarioLogado();
        return usuario == null ? null : usuario.getId();
    }

    @Override
    @Transactional
    public ConviteAceitoDTO aceitarConvitePorToken(String token) {
        Usuario usuarioLogado = Context.getUsuarioLogado();
        if (usuarioLogado == null || usuarioLogado.getId() == null) {
            throw new RestrictionException("Entre na sua conta para aceitar o convite.");
        }

        Optional<ConviteParaUsuarioIngressarBanda> notificacaoOpt = notificacaoDao.findConviteByLinkToken(token);
        if (notificacaoOpt.isPresent()) {
            return aceitarConviteDeNotificacao(notificacaoOpt.get(), usuarioLogado);
        }

        ConviteExternoBanda conviteExterno = buscarConviteExterno(token)
                .orElseThrow(() -> new ResourceNotFoundException("Convite não encontrado ou expirado."));
        return aceitarConviteExterno(conviteExterno, usuarioLogado);
    }

    private ConviteAceitoDTO aceitarConviteDeNotificacao(ConviteParaUsuarioIngressarBanda convite, Usuario usuarioLogado) {
        if (!convite.getDestinatarioId().equals(usuarioLogado.getId())) {
            throw new RestrictionException("Este convite não é para você.");
        }

        Banda banda = buscarBanda(convite.getRemetenteId());
        boolean jaEraMembro = ehMembro(usuarioLogado.getId(), banda.getId());
        if (jaRespondido(convite.getId()) && !jaEraMembro) {
            throw new RestrictionException("Este convite já foi respondido.");
        }

        ingressarNaBanda(usuarioLogado, banda, convite.getInstrumento());
        incluirUsuarioNosEventos(usuarioLogado, banda, convite.getEventos());

        convite.setLida(true);
        notificacaoDao.save(convite);
        registrarAceite(convite.getId());
        return new ConviteAceitoDTO(banda.getId(), banda.getNome(), jaEraMembro);
    }

    /** Aceitar pelo link fecha a notificacao no app: o card para de pedir uma resposta. */
    private void registrarAceite(Long idNotificacao) {
        if (idNotificacao == null || jaRespondido(idNotificacao)) {
            return;
        }
        RespostaNotificacao resposta = new RespostaNotificacao();
        resposta.setNotificacaoId(idNotificacao);
        resposta.setAcao(AcaoResposta.ACEITAR);
        respostaNotificacaoDao.save(resposta);
    }

    private ConviteAceitoDTO aceitarConviteExterno(ConviteExternoBanda convite, Usuario usuarioLogado) {
        Banda banda = convite.getBanda();
        boolean jaEraMembro = ehMembro(usuarioLogado.getId(), banda.getId());

        // Reabrir o proprio link depois de aceito e comum (WhatsApp, historico do navegador):
        // quem ja esta na banda so precisa ser levado ate ela.
        if (jaEraMembro && convite.isUtilizado()) {
            return new ConviteAceitoDTO(banda.getId(), banda.getNome(), true);
        }
        if (convite.isUtilizado()) {
            throw new RestrictionException("Este convite já foi utilizado.");
        }
        if (convite.isExpirado()) {
            throw new RestrictionException("Este convite expirou.");
        }

        ingressarNaBanda(usuarioLogado, banda, convite.getInstrumento());
        incluirUsuarioNosEventos(usuarioLogado, banda, convite.getEventos());

        convite.setIdUsuarioAceitou(usuarioLogado.getId());
        convite.setDataAceite(LocalDateTime.now());
        conviteExternoBandaDao.save(convite);
        return new ConviteAceitoDTO(banda.getId(), banda.getNome(), jaEraMembro);
    }

    /**
     * O flag "lida" da notificacao nao diz nada sobre o convite: abrir o painel de notificacoes
     * ja marca tudo como lido. Quem decide e a existencia de uma resposta (aceitar/recusar).
     */
    private boolean jaRespondido(Long idNotificacao) {
        return idNotificacao != null && respostaNotificacaoDao.existsByNotificacaoId(idNotificacao);
    }

    private boolean ehMembro(Long idUsuario, Long idBanda) {
        return musicoBandaDao.existsById(new MusicoBandaId(idUsuario, idBanda));
    }

    private void ingressarNaBanda(Usuario usuario, Banda banda, String instrumento) {
        if (ehMembro(usuario.getId(), banda.getId())) {
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

    private String montarLinkConvite(String token) {
        return frontendUrl + "/convite/" + token;
    }

    /**
     * O mesmo convite é alcançável pelo token do link ou pelo código curto digitado no app,
     * então quem chama não precisa saber qual dos dois recebeu.
     */
    private Optional<ConviteExternoBanda> buscarConviteExterno(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            return Optional.empty();
        }

        Optional<ConviteExternoBanda> porToken = conviteExternoBandaDao.findByToken(identificador);
        if (porToken.isPresent()) {
            return porToken;
        }

        String codigo = normalizarCodigo(identificador);
        if (codigo.length() != TAMANHO_CODIGO) {
            return Optional.empty();
        }
        return conviteExternoBandaDao.findByCodigo(codigo);
    }

    /** Aceita o código como o usuário digitar: com hífen, espaço ou minúsculas. */
    private String normalizarCodigo(String entrada) {
        return entrada.replaceAll("[^A-Za-z0-9]", "").toUpperCase();
    }

    /** "4K2PWX7N" -> "4K2P-WX7N": o hífen no meio reduz erro de digitação. */
    private String formatarCodigo(String codigo) {
        if (codigo == null || codigo.length() != TAMANHO_CODIGO) {
            return codigo;
        }
        int meio = TAMANHO_CODIGO / 2;
        return codigo.substring(0, meio) + "-" + codigo.substring(meio);
    }

    private String gerarCodigoUnico() {
        for (int tentativa = 0; tentativa < TENTATIVAS_CODIGO_UNICO; tentativa++) {
            String codigo = gerarCodigo();
            if (!conviteExternoBandaDao.existsByCodigo(codigo)) {
                return codigo;
            }
        }
        // Colidir 10 vezes seguidas em 31^8 possibilidades significa que algo está errado;
        // melhor gerar o convite só com link do que estourar a geração inteira.
        log.warn("Não foi possível gerar um código de convite único após {} tentativas", TENTATIVAS_CODIGO_UNICO);
        return null;
    }

    private String gerarCodigo() {
        StringBuilder codigo = new StringBuilder(TAMANHO_CODIGO);
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            codigo.append(ALFABETO_CODIGO.charAt(RANDOM.nextInt(ALFABETO_CODIGO.length())));
        }
        return codigo.toString();
    }
}
