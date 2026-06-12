package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.CustoOperacionalDao;
import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.dto.CustoOperacionalDTO;
import com.baseapplication.core.dto.CustoOperacionalRequestDTO;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.CustoOperacional;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.service.CustoOperacionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustoOperacionalServiceImpl implements CustoOperacionalService {

    private final CustoOperacionalDao custoOperacionalDao;
    private final ShowDao showDao;

    @Override
    public List<CustoOperacionalDTO> buscarPorShow(Long idShow) {
        return custoOperacionalDao.buscarPorShow(idShow).stream()
                .map(CustoOperacionalDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public CustoOperacionalDTO adicionar(CustoOperacionalRequestDTO dto) {
        Show show = showDao.findById(dto.getIdShow())
                .orElseThrow(() -> new ResourceNotFoundException("Show não encontrado"));
        CustoOperacional custo = new CustoOperacional();
        custo.setShow(show);
        custo.setDescricao(dto.getDescricao());
        custo.setValor(dto.getValor());
        return new CustoOperacionalDTO(custoOperacionalDao.save(custo));
    }

    @Override
    public CustoOperacionalDTO atualizar(Long id, CustoOperacionalRequestDTO dto) {
        CustoOperacional custo = custoOperacionalDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Custo operacional não encontrado"));
        custo.setDescricao(dto.getDescricao());
        custo.setValor(dto.getValor());
        return new CustoOperacionalDTO(custoOperacionalDao.save(custo));
    }

    @Override
    public void remover(Long id) {
        CustoOperacional custo = custoOperacionalDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Custo operacional não encontrado"));
        custoOperacionalDao.delete(custo);
    }
}
