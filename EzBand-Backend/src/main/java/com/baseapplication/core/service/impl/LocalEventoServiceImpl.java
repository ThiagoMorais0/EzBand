package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.LocalEventoDao;
import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.service.LocalEventoService;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocalEventoServiceImpl implements LocalEventoService {

    private final LocalEventoDao dao;

    @Override
    public void cadastrar(LocalEvento estudio) {
        dao.save(estudio);
    }

    @Override
    public List<LocalEvento> buscarLocalEventosDoUsuario() {
        return dao.findByProprietario(Context.getUsuarioLogado().getId());
    }

    @Override
    public List<LocalEvento> buscarTodos() {
        return dao.findAll();
    }

    @Override
    public void deletarTodos() {
        dao.deleteAll();
    }

    @Override
    public LocalEvento buscarPorId(Long id) {
        return dao.findById(id).orElse(new LocalEvento());
    }

    @Override
    public void editar(LocalEventoDTO estudioDTO) {
        LocalEvento estudio = dao.findById(estudioDTO.getId()).orElseThrow();
        estudioDTO.toEntity(estudio);
        dao.save(estudio);
    }

    @Override
    public List<ShowDTO> buscarShowsPorLocalEvento(Long idLocalEvento) {
        return null;
    }
}
