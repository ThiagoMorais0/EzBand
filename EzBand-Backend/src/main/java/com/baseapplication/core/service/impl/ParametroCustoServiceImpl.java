package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.ParametroCustoDao;
import com.baseapplication.core.model.ParametroCusto;
import com.baseapplication.core.service.ParametroCustoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParametroCustoServiceImpl implements ParametroCustoService {
    private final ParametroCustoDao dao;


    @Override
    public void salvar(ParametroCusto parametroCusto) {
        dao.save(parametroCusto);
    }

    @Override
    public void deletar(ParametroCusto parametroCusto) {
        dao.delete(parametroCusto);
    }

    @Override
    public List<ParametroCusto> buscarPorIdBanda(Long idBanda) {
        return dao.buscarPorIdBanda(idBanda);
    }

    @Override
    public ParametroCusto buscarPorId(Long id) {
        return dao.findById(id).orElseThrow();
    }

    @Override
    public ParametroCusto buscarPorNome(String nome) {
        return dao.findByNome(nome);
    }
}
