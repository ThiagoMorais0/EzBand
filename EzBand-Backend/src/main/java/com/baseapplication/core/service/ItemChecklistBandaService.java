package com.baseapplication.core.service;

import com.baseapplication.core.dto.CadastroItemChecklistBandaDTO;
import com.baseapplication.core.dto.ItemChecklistBandaDTO;

import java.util.List;

public interface ItemChecklistBandaService {
    
    List<ItemChecklistBandaDTO> buscarPorIdBanda(Long idBanda);
    
    ItemChecklistBandaDTO buscarPorId(Long id);
    
    ItemChecklistBandaDTO cadastrar(CadastroItemChecklistBandaDTO dto);
    
    ItemChecklistBandaDTO atualizar(Long id, CadastroItemChecklistBandaDTO dto);
    
    void deletar(Long id);
    
    void ativarDesativar(Long id, Boolean ativo);
}
