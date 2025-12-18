package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.*;
import com.baseapplication.core.dto.AvaliacaoEventoDTO;
import com.baseapplication.core.dto.PendenciaAvaliacaoEventoDTO;
import com.baseapplication.core.dto.ResumoPendenciasAvaliacaoDTO;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.*;
import com.baseapplication.core.service.AvaliacaoEventoService;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AvaliacaoEventoServiceImpl implements AvaliacaoEventoService {
    
    private final PendenciaAvaliacaoEventoDao pendenciaDao;
    private final AvaliacaoEventoDao avaliacaoDao;
    private final BandaDao bandaDao;
    private final ShowDao showDao;
    private final EnsaioDao ensaioDao;
    private final AvaliacaoLocalEventoDao avaliacaoLocalEventoDao;
    private final AvaliacaoEstudioDao avaliacaoEstudioDao;
    
    @Override
    public List<PendenciaAvaliacaoEventoDTO> buscarPendenciasPorBanda(Long idBanda) {
        List<PendenciaAvaliacaoEvento> pendencias = pendenciaDao.buscarPendenciasPorBanda(
            idBanda, 
            LocalDateTime.now()
        );
        
        return pendencias.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public ResumoPendenciasAvaliacaoDTO buscarPendenciasDoUsuario() {
        Long idUsuario = Context.getUsuarioLogado().getId();
        List<PendenciaAvaliacaoEvento> pendencias = pendenciaDao.buscarPendenciasPorUsuario(
            idUsuario, 
            LocalDateTime.now()
        );
        
        List<PendenciaAvaliacaoEventoDTO> pendenciasDTO = pendencias.stream()
            .map(this::converterParaDTO)
            .collect(Collectors.toList());
        
        long pendenciasShows = pendenciasDTO.stream()
            .filter(p -> p.getTipoEvento() == TipoEvento.SHOW)
            .count();
        
        long pendenciasEnsaios = pendenciasDTO.stream()
            .filter(p -> p.getTipoEvento() == TipoEvento.ENSAIO)
            .count();
        
        return new ResumoPendenciasAvaliacaoDTO(
            pendenciasDTO.size(),
            (int) pendenciasShows,
            (int) pendenciasEnsaios,
            pendenciasDTO
        );
    }
    
    @Override
    @Transactional
    public void adiarPendencia(Long idPendencia) {
        PendenciaAvaliacaoEvento pendencia = pendenciaDao.findById(idPendencia)
            .orElseThrow(() -> new RuntimeException("Pendência não encontrada"));
        
        // Verifica se o usuário pertence à banda
        verificarPermissaoBanda(pendencia.getBanda().getId());
        
        // Verifica se ainda pode adiar (máximo 3 vezes)
        if (pendencia.getQuantidadeAdiamentos() >= 3) {
            throw new RuntimeException("Limite de adiamentos atingido. Por favor, avalie o evento.");
        }
        
        // Adia por 7 dias
        pendencia.setDataAdiamento(LocalDateTime.now().plusDays(7));
        pendencia.setQuantidadeAdiamentos(pendencia.getQuantidadeAdiamentos() + 1);
        
        pendenciaDao.save(pendencia);
    }
    
    @Override
    @Transactional
    public AvaliacaoEventoDTO avaliarEvento(AvaliacaoEventoDTO avaliacaoDTO) {
        // Verifica se o usuário pertence à banda
        verificarPermissaoBanda(avaliacaoDTO.getIdBanda());
        
        // Verifica se já existe avaliação para este evento
        avaliacaoDao.findByIdEventoAndTipoEventoAndBandaId(
            avaliacaoDTO.getIdEvento(),
            avaliacaoDTO.getTipoEvento(),
            avaliacaoDTO.getIdBanda()
        ).ifPresent(a -> {
            throw new RuntimeException("Este evento já foi avaliado por esta banda");
        });
        
        // Busca a banda
        Banda banda = bandaDao.findById(avaliacaoDTO.getIdBanda())
            .orElseThrow(() -> new RuntimeException("Banda não encontrada"));
        
        // Cria a avaliação do evento
        AvaliacaoEvento avaliacao = new AvaliacaoEvento();
        avaliacao.setIdEvento(avaliacaoDTO.getIdEvento());
        avaliacao.setTipoEvento(avaliacaoDTO.getTipoEvento());
        avaliacao.setBanda(banda);
        avaliacao.setUsuarioAvaliador(Context.getUsuarioLogado());
        avaliacao.setQualidadeSom(avaliacaoDTO.getQualidadeSom());
        avaliacao.setEstrutura(avaliacaoDTO.getEstrutura());
        avaliacao.setOrganizacao(avaliacaoDTO.getOrganizacao());
        avaliacao.setAtendimento(avaliacaoDTO.getAtendimento());
        avaliacao.setExperienciaGeral(avaliacaoDTO.getExperienciaGeral());
        avaliacao.setComentarioPositivo(avaliacaoDTO.getComentarioPositivo());
        avaliacao.setComentarioNegativo(avaliacaoDTO.getComentarioNegativo());
        avaliacao.setObservacoes(avaliacaoDTO.getObservacoes());
        avaliacao.setDataAvaliacao(LocalDateTime.now());
        
        avaliacao = avaliacaoDao.save(avaliacao);
        
        // Marca a pendência como avaliada
        pendenciaDao.findByIdEventoAndTipoEventoAndBandaIdAndAvaliadoFalse(
            avaliacaoDTO.getIdEvento(),
            avaliacaoDTO.getTipoEvento(),
            avaliacaoDTO.getIdBanda()
        ).ifPresent(pendencia -> {
            pendencia.setAvaliado(true);
            pendencia.setDataAvaliacao(LocalDateTime.now());
            pendenciaDao.save(pendencia);
        });
        
        // Se houver avaliação do local/estúdio, cria também a avaliação específica
        if (avaliacaoDTO.getQualidadeSom() != null || avaliacaoDTO.getEstrutura() != null ||
            avaliacaoDTO.getOrganizacao() != null || avaliacaoDTO.getAtendimento() != null) {
            
            if (avaliacaoDTO.getTipoEvento() == TipoEvento.SHOW) {
                criarAvaliacaoLocalEvento(avaliacaoDTO);
            } else if (avaliacaoDTO.getTipoEvento() == TipoEvento.ENSAIO) {
                criarAvaliacaoEstudio(avaliacaoDTO);
            }
        }
        
        return new AvaliacaoEventoDTO(avaliacao);
    }
    
    @Override
    public List<AvaliacaoEventoDTO> buscarAvaliacoesDoEvento(Long idEvento, String tipoEvento) {
        TipoEvento tipo = TipoEvento.valueOf(tipoEvento.toUpperCase());
        
        return avaliacaoDao.findByIdEventoAndTipoEventoOrderByDataAvaliacaoDesc(idEvento, tipo)
            .stream()
            .map(AvaliacaoEventoDTO::new)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<AvaliacaoEventoDTO> buscarAvaliacoesDaBanda(Long idBanda) {
        return avaliacaoDao.findByBandaIdOrderByDataAvaliacaoDesc(idBanda)
            .stream()
            .map(this::converterAvaliacaoParaDTOComLocal)
            .collect(Collectors.toList());
    }
    
    @Override
    public Long contarPendenciasDoUsuario() {
        Long idUsuario = Context.getUsuarioLogado().getId();
        return pendenciaDao.contarPendenciasPorUsuario(idUsuario, LocalDateTime.now());
    }
    
    // Métodos auxiliares
    
    private PendenciaAvaliacaoEventoDTO converterParaDTO(PendenciaAvaliacaoEvento pendencia) {
        String localEvento = obterLocalEvento(pendencia.getIdEvento(), pendencia.getTipoEvento());
        return new PendenciaAvaliacaoEventoDTO(pendencia, localEvento);
    }
    
    private String obterLocalEvento(Long idEvento, TipoEvento tipoEvento) {
        if (tipoEvento == TipoEvento.SHOW) {
            return showDao.findById(idEvento)
                .map(show -> show.getLocal() != null ? show.getLocal() : 
                    (show.getLocalEvento() != null ? show.getLocalEvento().getNome() : "Local não informado"))
                .orElse("Local não encontrado");
        } else {
            return ensaioDao.findById(idEvento)
                .map(ensaio -> ensaio.getLocal() != null ? ensaio.getLocal() : 
                    (ensaio.getEstudio() != null ? ensaio.getEstudio().getNome() : "Local não informado"))
                .orElse("Local não encontrado");
        }
    }
    
    private AvaliacaoEventoDTO converterAvaliacaoParaDTOComLocal(AvaliacaoEvento avaliacao) {
        AvaliacaoEventoDTO dto = new AvaliacaoEventoDTO(avaliacao);
        
        // Busca informações do local avaliado
        if (avaliacao.getTipoEvento() == TipoEvento.SHOW) {
            showDao.findById(avaliacao.getIdEvento()).ifPresent(show -> {
                if (show.getLocalEvento() != null) {
                    dto.setNomeLocal(show.getLocalEvento().getNome());
                    dto.setIdLocal(show.getLocalEvento().getId());
                    dto.setTipoLocal("LOCAL_EVENTO");
                } else if (show.getLocal() != null) {
                    dto.setNomeLocal(show.getLocal());
                    dto.setIdLocal(null);
                    dto.setTipoLocal(null);
                }
            });
        } else if (avaliacao.getTipoEvento() == TipoEvento.ENSAIO) {
            ensaioDao.findById(avaliacao.getIdEvento()).ifPresent(ensaio -> {
                if (ensaio.getEstudio() != null) {
                    dto.setNomeLocal(ensaio.getEstudio().getNome());
                    dto.setIdLocal(ensaio.getEstudio().getId());
                    dto.setTipoLocal("ESTUDIO");
                } else if (ensaio.getLocal() != null) {
                    dto.setNomeLocal(ensaio.getLocal());
                    dto.setIdLocal(null);
                    dto.setTipoLocal(null);
                }
            });
        }
        
        return dto;
    }
    
    private void verificarPermissaoBanda(Long idBanda) {
        Long idUsuario = Context.getUsuarioLogado().getId();
        boolean pertenceABanda = Context.getUsuarioLogado().getBandas().stream()
            .anyMatch(banda -> banda.getId().equals(idBanda));
        
        if (!pertenceABanda) {
            throw new RuntimeException("Você não tem permissão para avaliar eventos desta banda");
        }
    }
    
    private void criarAvaliacaoLocalEvento(AvaliacaoEventoDTO avaliacaoDTO) {
        showDao.findById(avaliacaoDTO.getIdEvento()).ifPresent(show -> {
            if (show.getLocalEvento() != null) {
                // Verifica se o usuário já avaliou este local
                boolean jaAvaliou = avaliacaoLocalEventoDao.findByUsuarioIdAndLocalEventoId(
                    Context.getUsuarioLogado().getId(),
                    show.getLocalEvento().getId()
                ).isPresent();
                
                if (!jaAvaliou) {
                    AvaliacaoLocalEvento avaliacaoLocal = new AvaliacaoLocalEvento();
                    avaliacaoLocal.setUsuario(Context.getUsuarioLogado());
                    avaliacaoLocal.setLocalEvento(show.getLocalEvento());
                    avaliacaoLocal.setQualidadeSom(avaliacaoDTO.getQualidadeSom() != null ? avaliacaoDTO.getQualidadeSom() : 0);
                    avaliacaoLocal.setEstrutura(avaliacaoDTO.getEstrutura() != null ? avaliacaoDTO.getEstrutura() : 0);
                    avaliacaoLocal.setOrganizacao(avaliacaoDTO.getOrganizacao() != null ? avaliacaoDTO.getOrganizacao() : 0);
                    avaliacaoLocal.setAtendimento(avaliacaoDTO.getAtendimento() != null ? avaliacaoDTO.getAtendimento() : 0);
                    avaliacaoLocal.setExperienciaGeral(avaliacaoDTO.getExperienciaGeral());
                    avaliacaoLocal.setDataAvaliacao(java.time.LocalDate.now());
                    
                    String comentario = "";
                    if (avaliacaoDTO.getComentarioPositivo() != null) {
                        comentario += "Positivo: " + avaliacaoDTO.getComentarioPositivo() + " ";
                    }
                    if (avaliacaoDTO.getComentarioNegativo() != null) {
                        comentario += "Negativo: " + avaliacaoDTO.getComentarioNegativo();
                    }
                    avaliacaoLocal.setComentario(comentario.trim());
                    
                    avaliacaoLocalEventoDao.save(avaliacaoLocal);
                }
            }
        });
    }
    
    private void criarAvaliacaoEstudio(AvaliacaoEventoDTO avaliacaoDTO) {
        ensaioDao.findById(avaliacaoDTO.getIdEvento()).ifPresent(ensaio -> {
            if (ensaio.getEstudio() != null) {
                // Verifica se o usuário já avaliou este estúdio
                boolean jaAvaliou = avaliacaoEstudioDao.findByUsuarioIdAndEstudioId(
                    Context.getUsuarioLogado().getId(),
                    ensaio.getEstudio().getId()
                ).isPresent();
                
                if (!jaAvaliou) {
                    AvaliacaoEstudio avaliacaoEstudio = new AvaliacaoEstudio();
                    avaliacaoEstudio.setUsuario(Context.getUsuarioLogado());
                    avaliacaoEstudio.setEstudio(ensaio.getEstudio());
                    avaliacaoEstudio.setQualidadeSom(avaliacaoDTO.getQualidadeSom() != null ? avaliacaoDTO.getQualidadeSom() : 0);
                    avaliacaoEstudio.setEstrutura(avaliacaoDTO.getEstrutura() != null ? avaliacaoDTO.getEstrutura() : 0);
                    avaliacaoEstudio.setOrganizacao(avaliacaoDTO.getOrganizacao() != null ? avaliacaoDTO.getOrganizacao() : 0);
                    avaliacaoEstudio.setAtendimento(avaliacaoDTO.getAtendimento() != null ? avaliacaoDTO.getAtendimento() : 0);
                    avaliacaoEstudio.setExperienciaGeral(avaliacaoDTO.getExperienciaGeral());
                    avaliacaoEstudio.setDataAvaliacao(java.time.LocalDate.now());
                    
                    String comentario = "";
                    if (avaliacaoDTO.getComentarioPositivo() != null) {
                        comentario += "Positivo: " + avaliacaoDTO.getComentarioPositivo() + " ";
                    }
                    if (avaliacaoDTO.getComentarioNegativo() != null) {
                        comentario += "Negativo: " + avaliacaoDTO.getComentarioNegativo();
                    }
                    avaliacaoEstudio.setComentario(comentario.trim());
                    
                    avaliacaoEstudioDao.save(avaliacaoEstudio);
                }
            }
        });
    }
}
