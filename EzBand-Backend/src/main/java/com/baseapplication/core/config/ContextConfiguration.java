package com.baseapplication.core.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.utils.Context;

import jakarta.annotation.PostConstruct;

@Configuration
public class ContextConfiguration {

    @Autowired
    private UsuarioDao usuarioDao;

    @PostConstruct
    public void init() {
        Context.setUsuarioDao(usuarioDao);
    }
}
