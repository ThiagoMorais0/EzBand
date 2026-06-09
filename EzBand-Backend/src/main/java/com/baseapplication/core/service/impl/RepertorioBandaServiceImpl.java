package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.RepertorioBandaDao;
import com.baseapplication.core.model.RepertorioBanda;
import com.baseapplication.core.service.RepertorioBandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepertorioBandaServiceImpl implements RepertorioBandaService {

    @Autowired
    private RepertorioBandaDao repertorioBandaDao;

    @Override
    public void salvar(RepertorioBanda repertorioBanda) {
        repertorioBandaDao.save(repertorioBanda);
    }

    @Override
    public RepertorioBanda buscarPorId(Long id) {
        return repertorioBandaDao.findById(id).orElseThrow();
    }

    @Override
    public void deletar(RepertorioBanda repertorioBanda) {
        repertorioBandaDao.delete(repertorioBanda);
    }

    @Override
    public Integer buscarUltimoIndice(Long idBanda) {
        Integer indice = repertorioBandaDao.buscarUltimoIndice(idBanda);
        return indice == null ? 0 : indice;
    }

    @Override
    public void updateIndice(Long id, Integer indice) {
        repertorioBandaDao.updateIndice(id, indice);
    }

}
