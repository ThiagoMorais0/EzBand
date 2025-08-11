package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.EstudioDTO;
import com.baseapplication.core.dao.EstudioDao;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.service.EstudioService;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudioServiceImpl implements EstudioService {

    private final EstudioDao dao;

    @Override
    public void cadastrar(Estudio estudio) {
        dao.save(estudio);
    }

    @Override
    public List<Estudio> buscarEstudiosDoUsuario() {
        return dao.findByProprietario(Context.getUsuarioLogado().getId());
    }

    @Override
    public List<Estudio> buscarTodos() {
        return dao.findAll();
    }

    @Override
    public void deletarTodos() {
        dao.deleteAll();
    }

    @Override
    public Estudio buscarPorId(Long id) {
        return dao.findById(id).orElse(new Estudio());
    }

    @Override
    public void editar(EstudioDTO estudioDTO) {
        Estudio estudio = dao.findById(estudioDTO.getId()).orElseThrow();
        estudioDTO.toEntity(estudio);
        dao.save(estudio);
    }
}
