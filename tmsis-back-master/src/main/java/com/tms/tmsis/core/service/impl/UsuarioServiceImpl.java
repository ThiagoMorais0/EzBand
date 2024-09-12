package com.tms.tmsis.core.service.impl;

import com.tms.tmsis.core.dao.UsuarioDao;
import com.tms.tmsis.core.dto.RetornoDTO;
import com.tms.tmsis.core.model.Usuario;
import com.tms.tmsis.core.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    UsuarioDao usuarioDao;

    @Override
    public Usuario findByLogin(String login) {
        return usuarioDao.findByUsername(login);
    }

    @Override
    public Usuario findByEmail(String email) {
        return usuarioDao.findByEmail(email);
    }

    @Override
    public void salvar(Usuario usuario) {
        usuarioDao.save(usuario);
    }
}
