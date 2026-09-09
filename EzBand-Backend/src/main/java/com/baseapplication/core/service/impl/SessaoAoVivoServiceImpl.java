package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.MusicoBandaDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dao.SessaoAoVivoDao;
import com.baseapplication.core.dto.live.EdicaoResumoDTO;
import com.baseapplication.core.dto.live.LiveFaixaInfo;
import com.baseapplication.core.dto.live.LiveFaixaTocada;
import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import com.baseapplication.core.dto.live.ResumoSessaoDTO;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.MusicoBandaId;
import com.baseapplication.core.model.SessaoAoVivo;
import com.baseapplication.core.model.SessaoAoVivoFaixa;
import com.baseapplication.core.service.SessaoAoVivoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SessaoAoVivoServiceImpl implements SessaoAoVivoService {

    private final SessaoAoVivoDao sessaoAoVivoDao;
    private final MusicoBandaDao musicoBandaDao;
    private final UsuarioDao usuarioDao;

    public SessaoAoVivoServiceImpl(SessaoAoVivoDao sessaoAoVivoDao, MusicoBandaDao musicoBandaDao,
                                   UsuarioDao usuarioDao) {
        this.sessaoAoVivoDao = sessaoAoVivoDao;
        this.musicoBandaDao = musicoBandaDao;
        this.usuarioDao = usuarioDao;
    }

    /**
     * Este é o único ponto do Modo Performance que escreve no Postgres, e roda uma vez por
     * show. O snapshot já traz título, artista e duração cadastrada de cada faixa — congelados
     * na abertura da sessão —, então não há uma segunda consulta ao repertório aqui.
     */
    @Override
    @Transactional
    public Long persistir(LiveSessionSnapshot snapshot) {
        if (snapshot == null || snapshot.getTocadas().isEmpty()) return null;

        LocalDateTime iniciadaEm = paraLocal(snapshot.getIniciadaEm());

        // O encerramento explícito e o varredor de salas abandonadas podem correr juntos.
        if (sessaoAoVivoDao.existsByIdEventoAndTipoEventoAndIniciadaEm(
                snapshot.getIdEvento(), snapshot.getTipoEvento(), iniciadaEm)) {
            return null;
        }

        SessaoAoVivo sessao = new SessaoAoVivo();
        sessao.setIdEvento(snapshot.getIdEvento());
        sessao.setTipoEvento(snapshot.getTipoEvento());
        sessao.setIdBanda(snapshot.getIdBanda());
        sessao.setIdUsuarioIniciou(snapshot.getIniciadaPor());
        sessao.setIniciadaEm(iniciadaEm);
        sessao.setFinalizadaEm(LocalDateTime.now());
        sessao.setTotalFaixasRepertorio(snapshot.getTotalFaixas());

        long fimDaSessao = System.currentTimeMillis();
        int ordem = 0;
        for (LiveFaixaTocada tocada : snapshot.getTocadas()) {
            sessao.adicionarFaixa(montarFaixa(snapshot, tocada, fimDaSessao, ordem++));
        }

        Long id = sessaoAoVivoDao.save(sessao).getId();
        log.info("Resumo da sessão ao vivo {}:{} gravado ({} faixas)",
                snapshot.getTipoEvento(), snapshot.getIdEvento(), sessao.getFaixas().size());
        return id;
    }

    private SessaoAoVivoFaixa montarFaixa(LiveSessionSnapshot snapshot, LiveFaixaTocada tocada,
                                          long fimDaSessao, int ordem) {
        SessaoAoVivoFaixa faixa = new SessaoAoVivoFaixa();
        faixa.setIndice(tocada.getIdx());
        faixa.setOrdem(ordem);
        faixa.setAdicionadaManualmente(false);
        faixa.setIniciadaEm(paraLocal(tocada.getDe()));

        // A faixa que ainda estava aberta no encerramento fecha no instante do encerramento.
        long fim = tocada.getAte() != null ? tocada.getAte() : fimDaSessao;
        faixa.setFinalizadaEm(paraLocal(fim));
        if (tocada.getDe() != null) {
            faixa.setDuracaoRealSegundos((int) Math.max(0, (fim - tocada.getDe()) / 1000));
        }

        LiveFaixaInfo info = snapshot.faixa(tocada.getIdx());
        faixa.setTitulo(info != null ? info.getTitulo() : tocada.getTitulo());
        faixa.setArtista(info != null ? info.getArtista() : null);
        faixa.setDuracaoCadastradaSegundos(info != null ? info.getDuracaoSegundos() : null);
        return faixa;
    }

    /**
     * Aplica a versão corrigida do resumo.
     *
     * <p>Diff declarativo: o que veio com {@code id} é atualizado, o que veio sem {@code id}
     * vira faixa nova marcada como manual, e o que existia e não veio foi removido. A ordem
     * da lista recebida vira o campo {@code ordem} — {@code iniciadaEm} não é tocado, porque
     * corrigir o resumo não muda a hora em que a música aconteceu.
     */
    @Override
    @Transactional
    public ResumoSessaoDTO editarResumo(Long idSessao, EdicaoResumoDTO edicao, Long idUsuario) {
        SessaoAoVivo sessao = sessaoAoVivoDao.findById(idSessao)
                .orElseThrow(() -> new InternalException("Resumo não encontrado."));

        exigirAdminDaBanda(sessao.getIdBanda(), idUsuario);

        List<EdicaoResumoDTO.FaixaEditadaDTO> recebidas = edicao == null || edicao.getFaixas() == null
                ? List.of() : edicao.getFaixas();

        Map<Long, SessaoAoVivoFaixa> existentes = sessao.getFaixas().stream()
                .filter(f -> f.getId() != null)
                .collect(Collectors.toMap(SessaoAoVivoFaixa::getId, f -> f));

        List<SessaoAoVivoFaixa> resultado = new ArrayList<>(recebidas.size());
        int ordem = 0;

        for (EdicaoResumoDTO.FaixaEditadaDTO recebida : recebidas) {
            if (!recebida.temTitulo()) continue;

            SessaoAoVivoFaixa faixa = recebida.getId() == null ? null : existentes.get(recebida.getId());
            if (faixa == null) {
                // Sem id (ou com id de outra sessão): faixa que a banda tocou sem abrir no app.
                faixa = new SessaoAoVivoFaixa();
                faixa.setSessao(sessao);
                faixa.setAdicionadaManualmente(true);
            }

            faixa.setTitulo(recebida.getTitulo().trim());
            faixa.setArtista(recebida.getArtista() == null ? null : recebida.getArtista().trim());
            faixa.setDuracaoRealSegundos(naoNegativo(recebida.getDuracaoRealSegundos()));
            faixa.setOrdem(ordem++);
            resultado.add(faixa);
        }

        // orphanRemoval cuida de apagar o que ficou de fora — inclusive a repetição que o
        // usuário registrou sem querer, que é o caso que motivou esta tela existir.
        sessao.getFaixas().clear();
        sessao.getFaixas().addAll(resultado);
        sessao.setEditadoEm(LocalDateTime.now());
        sessao.setEditadoPor(idUsuario);

        SessaoAoVivo salva = sessaoAoVivoDao.save(sessao);
        log.info("Resumo {} corrigido por usuário {} ({} faixas)", idSessao, idUsuario, resultado.size());
        return enriquecer(new ResumoSessaoDTO(salva), salva.getIdBanda(), idUsuario);
    }

    /** Mesma régua de quem edita repertório e shows: administrador ou fundador da banda. */
    private void exigirAdminDaBanda(Long idBanda, Long idUsuario) {
        if (!ehAdminDaBanda(idBanda, idUsuario)) {
            throw new InternalException("Usuário não tem permissão para corrigir este resumo.");
        }
    }

    private boolean ehAdminDaBanda(Long idBanda, Long idUsuario) {
        if (idBanda == null || idUsuario == null) return false;
        MusicoBanda membro = musicoBandaDao.findById(new MusicoBandaId(idUsuario, idBanda)).orElse(null);
        return membro != null && membro.getPermissoes() != null
                && (membro.getPermissoes().contains(PermissaoMusico.ADMINISTRADOR)
                 || membro.getPermissoes().contains(PermissaoMusico.FUNDADOR));
    }

    private Integer naoNegativo(Integer valor) {
        if (valor == null) return null;
        return valor < 0 ? null : valor;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumoSessaoDTO buscarUltimoDoEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuario) {
        return sessaoAoVivoDao.buscarPorEvento(idEvento, tipoEvento).stream()
                .findFirst()
                .map(sessao -> enriquecer(new ResumoSessaoDTO(sessao), sessao.getIdBanda(), idUsuario))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumoSessaoDTO> buscarDoEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuario) {
        return sessaoAoVivoDao.buscarPorEvento(idEvento, tipoEvento).stream()
                .map(sessao -> enriquecer(new ResumoSessaoDTO(sessao), sessao.getIdBanda(), idUsuario))
                .toList();
    }

    /**
     * Acrescenta o que depende de quem está perguntando (pode editar?) e o nome de quem
     * corrigiu. A busca do nome só acontece em resumo de fato editado — que é a exceção.
     */
    private ResumoSessaoDTO enriquecer(ResumoSessaoDTO dto, Long idBanda, Long idUsuario) {
        dto.setPodeEditar(ehAdminDaBanda(idBanda, idUsuario));
        if (dto.getEditadoPor() != null) {
            usuarioDao.findById(dto.getEditadoPor())
                    .ifPresent(u -> dto.setEditadoPorNome(u.getNome()));
        }
        return dto;
    }

    private LocalDateTime paraLocal(Long epochMs) {
        if (epochMs == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.systemDefault());
    }
}
