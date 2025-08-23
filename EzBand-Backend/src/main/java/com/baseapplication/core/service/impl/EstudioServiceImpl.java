package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.CadastroEstudioDTO;
import com.baseapplication.core.dto.EstudioDTO;
import com.baseapplication.core.dao.EstudioDao;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.exception.ConflictException;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.service.EnsaioService;
import com.baseapplication.core.service.EstudioService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.FileUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EstudioServiceImpl implements EstudioService {

    private final EstudioDao dao;
    private final ImagemService imagemService;
    private final EnsaioService ensaioService;

    @Override
    public void cadastrar(Estudio estudio) {
        dao.save(estudio);
    }

    @Override
    public List<Estudio> buscarEstudiosDoUsuario() {
        return dao.findByProprietario(Context.getUsuarioLogado().getId());
    }

    @Override
    public List<Estudio> buscarTodos() {
        return dao.findAll();
    }

    @Override
    public void deletarTodos() {
        dao.deleteAll();
    }

    @Override
    public Estudio buscarPorId(Long id) {
        return dao.findById(id).orElse(new Estudio());
    }

    @Override
    public void editar(EstudioDTO estudioDTO) {
        Estudio estudio = dao.findById(estudioDTO.getId()).orElseThrow();
        estudioDTO.toEntity(estudio);
        dao.save(estudio);
    }

    @Override
    public ResponseEntity<?> cadastrarComImagem(CadastroEstudioDTO estudioDTO, MultipartFile imagem) {
        Estudio estudio =  estudioDTO.toEntity();
        cadastrar(estudio);
        String urlImagem = imagemService.saveImageAndGetUrl(imagem, "studiologo", estudio.getId() + "." + FileUtils.getSufix(imagem));
        estudio.setUrlFotoPerfil(urlImagem);
        cadastrar(estudio);
        return ResponseEntity.ok(null);
    }

    @Override
    public void editarComImagem(String estudioJson, MultipartFile imagem) {
        EstudioDTO estudioDTO = null;
        try {
            estudioDTO = new ObjectMapper().readValue(estudioJson, EstudioDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
        Estudio estudio = buscarPorId(estudioDTO.getId());
        String urlImagem = imagemService.saveImageAndGetUrl(imagem, "studiologo", estudioDTO.getId() + "." + FileUtils.getSufix(imagem));
        BeanUtils.copyProperties(estudioDTO, estudio);
        estudio.setUrlFotoPerfil(urlImagem);
        dao.save(estudio);
    }

    @Override
    public List<EnsaioDTO> buscarEnsaiosPorEstudio(Long idEstudio) {
        Estudio estudio = buscarPorId(idEstudio);
        if(estudio == null){
            throw new InternalException("Estúdio não encontrado");
        }
        return estudio.getEnsaios().stream().map(EnsaioDTO::new).toList();
    }
}
