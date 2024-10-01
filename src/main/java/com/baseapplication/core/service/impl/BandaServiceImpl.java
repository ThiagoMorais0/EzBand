package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.BandaDao;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.service.BandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BandaServiceImpl implements BandaService {

    @Autowired
    private BandaDao bandaDao;
    @Override
    public List<Banda> buscarBandasPorUsuario(Long idUsuario) {
        return bandaDao.buscarBandasPorUsuario(idUsuario).orElse(new ArrayList<Banda>());
    }

    @Override
    public List<Banda> buscarParticipacoesEspeciais(Long idUsuario) {
        return bandaDao.buscarParticipacoesEspeciais(idUsuario).orElse(new ArrayList<Banda>());
    }
}
