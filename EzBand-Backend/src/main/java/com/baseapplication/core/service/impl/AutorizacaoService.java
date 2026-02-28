package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.UserPrincipal;
import com.baseapplication.core.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AutorizacaoService implements UserDetailsService {

    @Autowired
    UsuarioDao usuarioDao;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioDao.findByEmail(email);
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado: " + email);
        }
        return UserPrincipal.fromUsuario(usuario);
    }
}
