package com.baseapplication.core.service;

import com.baseapplication.core.dto.CadastroItemChecklistUsuarioDTO;
import com.baseapplication.core.dto.ItemChecklistUsuarioDTO;

import java.util.List;

public interface ItemChecklistUsuarioService {
    
    List<ItemChecklistUsuarioDTO> buscarPorIdUsuario(Long idUsuario);
    
    ItemChecklistUsuarioDTO buscarPorId(Long id);
    
    ItemChecklistUsuarioDTO cadastrar(CadastroItemChecklistUsuarioDTO dto, Long idUsuario);
    
    ItemChecklistUsuarioDTO atualizar(Long id, CadastroItemChecklistUsuarioDTO dto);
    
    void deletar(Long id);
    
    void ativarDesativar(Long id, Boolean ativo);
}
