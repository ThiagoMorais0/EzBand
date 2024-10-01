package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowServiceImpl implements ShowService {

    @Autowired
    private ShowDao showDao;

    @Override
    public Show buscarPorId(Long idShow) {
        return showDao.findById(idShow).get();
    }

    @Override
    public List<Show> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
        return showDao.buscarPorIdUsuario(idUsuario)
                .stream()
                .filter(i -> i.getStatus().equals(StatusEvento.PENDENTE))
                .sorted(Comparator.comparing(Show::getData))
                .collect(Collectors.toList());
    }
}
