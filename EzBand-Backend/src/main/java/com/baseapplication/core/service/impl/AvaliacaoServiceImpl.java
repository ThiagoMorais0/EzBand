package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.AvaliacaoEstudioDao;
import com.baseapplication.core.dao.AvaliacaoLocalEventoDao;
import com.baseapplication.core.dto.AvaliacaoDTO;
import com.baseapplication.core.enums.TipoAvaliacao;
import com.baseapplication.core.model.AvaliacaoEstudio;
import com.baseapplication.core.service.AvaliacaoService;
import com.baseapplication.core.service.EstudioService;
import com.baseapplication.core.service.LocalEventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoEstudioDao avaliacaoEstudioDao;
    private final AvaliacaoLocalEventoDao avaliacaoLocalEventoDao;
    private final EstudioService estudioService;
    private final LocalEventoService localEventoService;

    @Override
    public void avaliar(AvaliacaoDTO avaliacao) {
        switch (avaliacao.getTipoAvaliacao()) {
            case ESTUDIO -> avaliacaoEstudioDao.save(avaliacao.toEntityEstudio());
            case LOCAL_EVENTO -> avaliacaoLocalEventoDao.save(avaliacao.toEntityLocalEvento());
        }
    }

    @Override
    public Double buscarMedia(Long id, TipoAvaliacao tipo) {
        try{
            switch (tipo){
                case ESTUDIO -> estudioService.buscarPorId(id).getMediaAvaliacoes();
                case LOCAL_EVENTO -> localEventoService.buscarEntidadePorId(id).getMediaAvaliacoes();
                default -> {return 0.0;}
            }
            return 0.0;
        }catch (NullPointerException nullPointerException){
            return 0.0;
        }catch (Exception e){
            throw e;
        }
    }
}
