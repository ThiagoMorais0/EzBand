package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.LocalEventoDao;
import com.baseapplication.core.dto.BuscaLocalEventoDTO;
import com.baseapplication.core.dto.CadastroLocalEventoDTO;
import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.LocalEventoService;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.FileUtils;
import com.fasterxml.jackson.databind.util.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocalEventoServiceImpl implements LocalEventoService {

    private final LocalEventoDao dao;
    private final ImagemService imagemService;

    @Override
    public LocalEvento cadastrar(LocalEvento estudio) {
        return dao.save(estudio);
    }

    @Override
    public List<LocalEvento> buscarLocalEventosDoUsuario() {
        return dao.findByProprietario(Context.getUsuarioLogado().getId());
    }

    @Override
    public List<LocalEvento> buscarTodos() {
        return dao.findAll();
    }

    @Override
    public void deletarTodos() {
        dao.deleteAll();
    }

    @Override
    public LocalEvento buscarPorId(Long id) {
        return dao.findById(id).orElse(new LocalEvento());
    }

    @Override
    public void editar(LocalEventoDTO estudioDTO) {
        LocalEvento estudio = dao.findById(estudioDTO.getId()).orElseThrow();
        //estudioDTO.toEntity(estudio);
        BeanUtils.copyProperties(estudioDTO, estudio);
        dao.save(estudio);
    }

    @Override
    public List<ShowDTO> buscarShowsPorLocalEvento(Long idLocalEvento) {
        //ordernar por data
        return buscarPorId(idLocalEvento).getShows().stream()
                .filter(show -> show.getData() != null)
                .map(ShowDTO::new)
                .sorted(Comparator.comparing(ShowDTO::getData))
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> cadastrarComImagem(CadastroLocalEventoDTO localEventoDTO, MultipartFile imagem) {
        LocalEvento localEvento =  localEventoDTO.toEntity();
        localEvento = cadastrar(localEvento);
        String urlImagem = imagemService.saveImageAndGetUrl(imagem, "venuelogo", localEvento.getId() + "." + FileUtils.getSufix(imagem));
        localEvento.setUrlFotoPerfil(urlImagem);
        cadastrar(localEvento);
        return ResponseEntity.ok(null);
    }

    @Override
    public List<LocalEvento> buscarSugestoes(BuscaLocalEventoDTO dto) {
        List<LocalEvento> resultados = dao.buscarSugestoes(dto);

        LevenshteinDistance levenshtein = new LevenshteinDistance();

        return resultados.stream()
                .sorted(Comparator.comparingInt(localEvento -> 
                    dto.getNome() != null && !dto.getNome().isEmpty() 
                        ? levenshtein.apply(dto.getNome().toLowerCase(), localEvento.getNome().toLowerCase())
                        : 0))
                .limit(20)
                .collect(Collectors.toList());
    }
}
