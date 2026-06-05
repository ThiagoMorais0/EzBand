package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.PreferenciaNotificacaoMembroDao;
import com.baseapplication.core.dto.PreferenciaNotificacaoDTO;
import com.baseapplication.core.model.PreferenciaNotificacaoMembro;
import com.baseapplication.core.service.PreferenciaNotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenciaNotificacaoServiceImpl implements PreferenciaNotificacaoService {

    private final PreferenciaNotificacaoMembroDao preferenciaDao;

    @Override
    @Transactional
    public PreferenciaNotificacaoDTO salvarOuAtualizar(PreferenciaNotificacaoDTO dto) throws BadRequestException {
        if (dto.getIdBanda() == null) {
            throw new BadRequestException("ID da banda é obrigatório");
        }
        
        if (dto.getIdUsuario() == null && dto.getIdMembroFantasma() == null) {
            throw new BadRequestException("É necessário informar idUsuario ou idMembroFantasma");
        }
        
        PreferenciaNotificacaoMembro preferencia;
        
        if (dto.getId() != null) {
            preferencia = preferenciaDao.findById(dto.getId())
                    .orElse(new PreferenciaNotificacaoMembro());
        } else {
            if (dto.getIdUsuario() != null) {
                preferencia = preferenciaDao.buscarPorBandaEUsuario(dto.getIdBanda(), dto.getIdUsuario())
                        .orElse(new PreferenciaNotificacaoMembro());
            } else {
                preferencia = preferenciaDao.buscarPorBandaEMembroFantasma(dto.getIdBanda(), dto.getIdMembroFantasma())
                        .orElse(new PreferenciaNotificacaoMembro());
            }
        }
        
        preferencia.setIdBanda(dto.getIdBanda());
        preferencia.setIdUsuario(dto.getIdUsuario());
        preferencia.setIdMembroFantasma(dto.getIdMembroFantasma());
        preferencia.setNotificarNovoEvento(dto.getNotificarNovoEvento());
        preferencia.setDiasAntecedenciaNotificacao(dto.getDiasAntecedenciaNotificacao());
        
        PreferenciaNotificacaoMembro saved = preferenciaDao.save(preferencia);
        
        log.info("Preferência de notificação salva: {}", saved.getId());
        
        return new PreferenciaNotificacaoDTO(saved);
    }

    @Override
    public PreferenciaNotificacaoDTO buscarPorBandaEUsuario(Long idBanda, Long idUsuario) {
        return preferenciaDao.buscarPorBandaEUsuario(idBanda, idUsuario)
                .map(PreferenciaNotificacaoDTO::new)
                .orElse(criarPreferenciaDefault(idBanda, idUsuario, null));
    }

    @Override
    public PreferenciaNotificacaoDTO buscarPorBandaEMembroFantasma(Long idBanda, Long idMembroFantasma) {
        return preferenciaDao.buscarPorBandaEMembroFantasma(idBanda, idMembroFantasma)
                .map(PreferenciaNotificacaoDTO::new)
                .orElse(criarPreferenciaDefault(idBanda, null, idMembroFantasma));
    }

    @Override
    public List<PreferenciaNotificacaoDTO> buscarPorBanda(Long idBanda) {
        return preferenciaDao.buscarPorBanda(idBanda).stream()
                .map(PreferenciaNotificacaoDTO::new)
                .collect(Collectors.toList());
    }
    
    private PreferenciaNotificacaoDTO criarPreferenciaDefault(Long idBanda, Long idUsuario, Long idMembroFantasma) {
        PreferenciaNotificacaoDTO dto = new PreferenciaNotificacaoDTO();
        dto.setIdBanda(idBanda);
        dto.setIdUsuario(idUsuario);
        dto.setIdMembroFantasma(idMembroFantasma);
        dto.setNotificarNovoEvento(true);
        dto.setDiasAntecedenciaNotificacao(List.of(1, 7));
        return dto;
    }
}
