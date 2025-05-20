package com.baseapplication.core.service.impl;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.service.RepertorioEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepertorioEventoServiceImpl implements RepertorioEventoService {

    @Autowired
    private RepertorioEventoDao repertorioEventoDao;

    @Override
    public void salvarLista(List<RepertorioEvento> repertorio) {
        repertorio.forEach(repertorioEventoDao::save);
    }

    @Override
    public void limparRepertorioEvento(Long idEvento, TipoEvento tipoEvento) {
        repertorioEventoDao.limparRepertorioEvento(idEvento, tipoEvento.toString());
    }


}
