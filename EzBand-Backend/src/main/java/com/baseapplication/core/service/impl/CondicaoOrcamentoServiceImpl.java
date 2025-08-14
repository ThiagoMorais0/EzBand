package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.CondicaoOrcamentoDao;
import com.baseapplication.core.model.CondicaoOrcamento;
import com.baseapplication.core.service.CondicaoOrcamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CondicaoOrcamentoServiceImpl implements CondicaoOrcamentoService {

    private final CondicaoOrcamentoDao dao;

    @Override
    public void salvar(CondicaoOrcamento condicao) {
        dao.save(condicao);
    }

    @Override
    public void deletar(CondicaoOrcamento condicao) {
        dao.delete(condicao);
    }


}
