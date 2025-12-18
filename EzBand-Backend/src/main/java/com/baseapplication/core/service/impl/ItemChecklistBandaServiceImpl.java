package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.ItemChecklistBandaDao;
import com.baseapplication.core.dto.CadastroItemChecklistBandaDTO;
import com.baseapplication.core.dto.ItemChecklistBandaDTO;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.ItemChecklistBanda;
import com.baseapplication.core.service.ItemChecklistBandaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemChecklistBandaServiceImpl implements ItemChecklistBandaService {
    
    private final ItemChecklistBandaDao itemChecklistBandaDao;
    
    @Override
    public List<ItemChecklistBandaDTO> buscarPorIdBanda(Long idBanda) {
        return itemChecklistBandaDao.buscarPorIdBanda(idBanda).stream()
                .map(ItemChecklistBandaDTO::new)
                .collect(Collectors.toList());
    }
    
    @Override
    public ItemChecklistBandaDTO buscarPorId(Long id) {
        ItemChecklistBanda item = itemChecklistBandaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        return new ItemChecklistBandaDTO(item);
    }
    
    @Override
    public ItemChecklistBandaDTO cadastrar(CadastroItemChecklistBandaDTO dto) {
        ItemChecklistBanda item = new ItemChecklistBanda();
        item.setIdBanda(dto.getIdBanda());
        item.setNome(dto.getNome());
        item.setDescricao(dto.getDescricao());
        item.setDataCriacao(LocalDateTime.now());
        item.setAtivo(true);
        
        ItemChecklistBanda itemSalvo = itemChecklistBandaDao.save(item);
        return new ItemChecklistBandaDTO(itemSalvo);
    }
    
    @Override
    public ItemChecklistBandaDTO atualizar(Long id, CadastroItemChecklistBandaDTO dto) {
        ItemChecklistBanda item = itemChecklistBandaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        
        item.setNome(dto.getNome());
        item.setDescricao(dto.getDescricao());
        
        ItemChecklistBanda itemAtualizado = itemChecklistBandaDao.save(item);
        return new ItemChecklistBandaDTO(itemAtualizado);
    }
    
    @Override
    public void deletar(Long id) {
        ItemChecklistBanda item = itemChecklistBandaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        itemChecklistBandaDao.delete(item);
    }
    
    @Override
    public void ativarDesativar(Long id, Boolean ativo) {
        ItemChecklistBanda item = itemChecklistBandaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado"));
        item.setAtivo(ativo);
        itemChecklistBandaDao.save(item);
    }
}
