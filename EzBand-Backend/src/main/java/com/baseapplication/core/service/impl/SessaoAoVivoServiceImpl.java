package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.SessaoAoVivoDao;
import com.baseapplication.core.dto.live.LiveFaixaInfo;
import com.baseapplication.core.dto.live.LiveFaixaTocada;
import com.baseapplication.core.dto.live.LiveSessionSnapshot;
import com.baseapplication.core.dto.live.ResumoSessaoDTO;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.SessaoAoVivo;
import com.baseapplication.core.model.SessaoAoVivoFaixa;
import com.baseapplication.core.service.SessaoAoVivoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
public class SessaoAoVivoServiceImpl implements SessaoAoVivoService {

    private final SessaoAoVivoDao sessaoAoVivoDao;

    public SessaoAoVivoServiceImpl(SessaoAoVivoDao sessaoAoVivoDao) {
        this.sessaoAoVivoDao = sessaoAoVivoDao;
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
        for (LiveFaixaTocada tocada : snapshot.getTocadas()) {
            sessao.adicionarFaixa(montarFaixa(snapshot, tocada, fimDaSessao));
        }

        Long id = sessaoAoVivoDao.save(sessao).getId();
        log.info("Resumo da sessão ao vivo {}:{} gravado ({} faixas)",
                snapshot.getTipoEvento(), snapshot.getIdEvento(), sessao.getFaixas().size());
        return id;
    }

    private SessaoAoVivoFaixa montarFaixa(LiveSessionSnapshot snapshot, LiveFaixaTocada tocada, long fimDaSessao) {
        SessaoAoVivoFaixa faixa = new SessaoAoVivoFaixa();
        faixa.setIndice(tocada.getIdx());
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

    @Override
    @Transactional(readOnly = true)
    public ResumoSessaoDTO buscarUltimoDoEvento(Long idEvento, TipoEvento tipoEvento) {
        return sessaoAoVivoDao.buscarPorEvento(idEvento, tipoEvento).stream()
                .findFirst()
                .map(ResumoSessaoDTO::new)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResumoSessaoDTO> buscarDoEvento(Long idEvento, TipoEvento tipoEvento) {
        return sessaoAoVivoDao.buscarPorEvento(idEvento, tipoEvento).stream()
                .map(ResumoSessaoDTO::new)
                .toList();
    }

    private LocalDateTime paraLocal(Long epochMs) {
        if (epochMs == null) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMs), ZoneId.systemDefault());
    }
}
