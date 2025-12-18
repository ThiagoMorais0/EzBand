package com.baseapplication.core.service;

import com.baseapplication.core.dto.CriacaoMembroFantasmaDTO;
import com.baseapplication.core.dto.EdicaoMembroFantasmaDTO;
import com.baseapplication.core.dto.MembroFantasmaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MembroFantasmaService {
    
    MembroFantasmaDTO criar(String membroJson, MultipartFile foto);
    
    MembroFantasmaDTO editar(EdicaoMembroFantasmaDTO dto);
    
    void deletar(Long id);
    
    List<MembroFantasmaDTO> buscarPorIdBanda(Long idBanda);
    
    MembroFantasmaDTO buscarPorId(Long id);
}
