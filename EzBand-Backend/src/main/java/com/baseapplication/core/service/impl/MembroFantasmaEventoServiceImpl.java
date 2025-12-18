package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.MembroFantasmaEventoDao;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.MembroFantasmaEvento;
import com.baseapplication.core.service.MembroFantasmaEventoService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembroFantasmaEventoServiceImpl implements MembroFantasmaEventoService {

    private final MembroFantasmaEventoDao membroFantasmaEventoDao;

    @Override
    public void salvar(MembroFantasmaEvento membroFantasmaEvento) {
        membroFantasmaEventoDao.save(membroFantasmaEvento);
    }

    @Override
    public void remover(MembroFantasmaEvento membroFantasmaEvento) {
        membroFantasmaEventoDao.delete(membroFantasmaEvento);
    }

    @Override
    @Transactional
    public void removerTodosMembrosFantasma(Long idEvento, TipoEvento tipoEvento) {
        membroFantasmaEventoDao.removerTodosMembrosFantasma(idEvento, tipoEvento.toString());
    }

    @Override
    public List<MembroFantasmaEvento> buscarMembrosFantasmaPorEvento(Long idEvento, TipoEvento tipoEvento) {
        return membroFantasmaEventoDao.buscarMembrosFantasmaPorEvento(idEvento, tipoEvento);
    }
}
