package com.baseapplication.core.service;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.MembroFantasmaEvento;

import java.util.List;

public interface MembroFantasmaEventoService {
    
    void salvar(MembroFantasmaEvento membroFantasmaEvento);
    
    void remover(MembroFantasmaEvento membroFantasmaEvento);
    
    void removerTodosMembrosFantasma(Long idEvento, TipoEvento tipoEvento);
    
    List<MembroFantasmaEvento> buscarMembrosFantasmaPorEvento(Long idEvento, TipoEvento tipoEvento);
}
