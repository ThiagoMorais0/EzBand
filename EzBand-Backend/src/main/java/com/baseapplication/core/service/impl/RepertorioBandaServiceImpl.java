package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.RepertorioBandaDao;
import com.baseapplication.core.model.RepertorioBanda;
import com.baseapplication.core.service.RepertorioBandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RepertorioBandaServiceImpl implements RepertorioBandaService {

    @Autowired
    private RepertorioBandaDao repertorioBandaDao;

    @Override
    public void salvar(RepertorioBanda repertorioBanda) {
        repertorioBandaDao.save(repertorioBanda);
    }
}
