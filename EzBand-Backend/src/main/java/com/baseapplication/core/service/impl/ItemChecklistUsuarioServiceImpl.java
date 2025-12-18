package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.ItemChecklistUsuarioDao;
import com.baseapplication.core.dto.CadastroItemChecklistUsuarioDTO;
import com.baseapplication.core.dto.ItemChecklistUsuarioDTO;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.ItemChecklistUsuario;
import com.baseapplication.core.service.ItemChecklistUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemChecklistUsuarioServiceImpl implements ItemChecklistUsuarioService {
    
    private final ItemChecklistUsuarioDao itemChecklistUsuarioDao;
    
    @Override
    public List<ItemChecklistUsuarioDTO> buscarPorIdUsuario(Long idUsuario) {
        return itemChecklistUsuarioDao.buscarPorIdUsuario(idUsuario).stream()
                .map(ItemChecklistUsuarioDTO::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public ItemChecklistUsuarioDTO buscarPorId(Long id) {
        ItemChecklistUsuario item = itemChecklistUsuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        return new ItemChecklistUsuarioDTO(item);
    }
    
    @Override
    public ItemChecklistUsuarioDTO cadastrar(CadastroItemChecklistUsuarioDTO dto, Long idUsuario) {
        ItemChecklistUsuario item = new ItemChecklistUsuario();
        item.setIdUsuario(idUsuario);
        item.setNome(dto.getNome());
        item.setDescricao(dto.getDescricao());
        item.setDataCriacao(LocalDateTime.now());
        item.setAtivo(true);
        
        ItemChecklistUsuario itemSalvo = itemChecklistUsuarioDao.save(item);
        return new ItemChecklistUsuarioDTO(itemSalvo);
    }
    
    @Override
    public ItemChecklistUsuarioDTO atualizar(Long id, CadastroItemChecklistUsuarioDTO dto) {
        ItemChecklistUsuario item = itemChecklistUsuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        
        item.setNome(dto.getNome());
        item.setDescricao(dto.getDescricao());
        
        ItemChecklistUsuario itemAtualizado = itemChecklistUsuarioDao.save(item);
        return new ItemChecklistUsuarioDTO(itemAtualizado);
    }
    
    @Override
    public void deletar(Long id) {
        ItemChecklistUsuario item = itemChecklistUsuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        itemChecklistUsuarioDao.delete(item);
    }
    
    @Override
    public void ativarDesativar(Long id, Boolean ativo) {
        ItemChecklistUsuario item = itemChecklistUsuarioDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        item.setAtivo(ativo);
        itemChecklistUsuarioDao.save(item);
    }
}
