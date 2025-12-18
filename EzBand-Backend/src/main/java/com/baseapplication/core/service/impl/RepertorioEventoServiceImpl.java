package com.baseapplication.core.service.impl;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.service.RepertorioEventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepertorioEventoServiceImpl implements RepertorioEventoService {

    @Autowired
    private RepertorioEventoDao repertorioEventoDao;

    @Override
    public void salvarLista(List<RepertorioEvento> repertorio) {
        repertorio.forEach(repertorioEventoDao::save);
    }

    @Override
    public void limparRepertorioEvento(Long idEvento, TipoEvento tipoEvento) {
        repertorioEventoDao.limparRepertorioEvento(idEvento, tipoEvento.toString());
    }

    @Override
    public List<RepertorioEvento> buscarPorEvento(Long idEvento, TipoEvento tipoEvento) {
        return repertorioEventoDao.buscarPorEvento(idEvento, tipoEvento);
    }

    @Override
    public RepertorioEvento buscarPorIndiceEEvento(Integer indice, Long idEvento, TipoEvento tipoEvento) {
        return repertorioEventoDao.buscarPorIndiceEEvento(indice, idEvento, tipoEvento);
    }

    @Override
    public void salvar(RepertorioEvento repertorioEvento) {
        repertorioEventoDao.save(repertorioEvento);
    }

    @Override
    public void removerMusicaDoRepertorio(Long idEvento, TipoEvento tipoEvento, Integer indice) {
        RepertorioEvento musica = buscarPorIndiceEEvento(indice, idEvento, tipoEvento);
        if (musica != null) {
            repertorioEventoDao.delete(musica);
        }
    }

}
