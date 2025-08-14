package com.baseapplication.core.service;

import com.baseapplication.core.model.ParametroCusto;
import org.springframework.web.servlet.tags.Param;

import java.util.List;

public interface ParametroCustoService {
    void salvar(ParametroCusto parametroCusto);
    void deletar(ParametroCusto parametroCusto);

    List<ParametroCusto> buscarPorIdBanda(Long idBanda);

    ParametroCusto buscarPorId(Long id);

    ParametroCusto buscarPorNome(String nome);
}
