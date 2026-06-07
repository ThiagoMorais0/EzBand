package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.MembroFantasmaDao;
import com.baseapplication.core.dto.CriacaoMembroFantasmaDTO;
import com.baseapplication.core.dto.EdicaoMembroFantasmaDTO;
import com.baseapplication.core.dto.MembroFantasmaDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.MembroFantasma;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.MembroFantasmaService;
import com.baseapplication.core.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembroFantasmaServiceImpl implements MembroFantasmaService {

    private final MembroFantasmaDao membroFantasmaDao;
    private final ImagemService imagemService;

    @Override
    @Transactional
    public MembroFantasmaDTO criar(String membroJson, MultipartFile foto) {
        CriacaoMembroFantasmaDTO dto;
        try {
            dto = new ObjectMapper().readValue(membroJson, CriacaoMembroFantasmaDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException("Erro ao converter json");
        }
        
        MembroFantasma membroFantasma = new MembroFantasma();
        BeanUtils.copyProperties(dto, membroFantasma);
        
        // Salvar primeiro para obter o ID
        MembroFantasma saved = membroFantasmaDao.save(membroFantasma);
        
        // Salvar imagem se fornecida
        String urlFoto;
        if (foto != null) {
            urlFoto = imagemService.saveImageAndGetUrl(foto, "profilepictures",
                    "membro_fantasma_" + saved.getId() + "." + FileUtils.getSufix(foto));
        } else {
            urlFoto = "default";
        }
        
        saved.setUrlFoto(urlFoto);
        saved = membroFantasmaDao.save(saved);
        
        return new MembroFantasmaDTO(saved);
    }
    
    @Override
    @Transactional
    public MembroFantasmaDTO editar(EdicaoMembroFantasmaDTO dto) {
        MembroFantasma membroFantasma = membroFantasmaDao.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Membro fantasma não encontrado"));

        if (dto.getNome() != null) {
            membroFantasma.setNome(dto.getNome());
        }
        if (dto.getInstrumento() != null) {
            membroFantasma.setInstrumento(dto.getInstrumento());
        }
        if (dto.getUrlFoto() != null) {
            imagemService.deletarImagemPorUrl(membroFantasma.getUrlFoto());
            membroFantasma.setUrlFoto(dto.getUrlFoto());
        }
        if (dto.getObservacoes() != null) {
            membroFantasma.setObservacoes(dto.getObservacoes());
        }
        if (dto.getCelular() != null) {
            String celularAtual = membroFantasma.getCelular();
            if (!dto.getCelular().equals(celularAtual)) {
                membroFantasma.setCelular(dto.getCelular());
                membroFantasma.setCelularValidado(false);
                membroFantasma.setDataCelularValidado(null);
            }
        }
        
        MembroFantasma saved = membroFantasmaDao.save(membroFantasma);
        return new MembroFantasmaDTO(saved);
    }

    @Override
    @Transactional
    public void deletar(Long id) {
        MembroFantasma membroFantasma = membroFantasmaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro fantasma não encontrado"));
        imagemService.deletarImagemPorUrl(membroFantasma.getUrlFoto());
        membroFantasmaDao.deleteById(id);
    }

    @Override
    public List<MembroFantasmaDTO> buscarPorIdBanda(Long idBanda) {
        return membroFantasmaDao.buscarPorIdBanda(idBanda).stream()
                .map(MembroFantasmaDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public MembroFantasmaDTO buscarPorId(Long id) {
        MembroFantasma membroFantasma = membroFantasmaDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro fantasma não encontrado"));
        return new MembroFantasmaDTO(membroFantasma);
    }
}
