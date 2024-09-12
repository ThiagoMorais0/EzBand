package com.baseapplication.core.service.impl;

import com.baseapplication.core.model.Show;
import com.baseapplication.core.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShowServiceImpl implements ShowService {

    @Override
    public Show buscarPorId(Long idEvento) {
        return new Show();
    }
}
