package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.BandaParticipacaoEspecialDTO;
import com.baseapplication.core.dto.EventosSeparadosDTO;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioPainelDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.dto.superClasses.NotificacaoDTO;
import com.baseapplication.core.service.*;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class UsuarioHelperServiceImpl implements UsuarioHelperService {

    @Autowired
    private UsuarioDao usuarioDao;

    public Usuario buscarPorContato(String contato, TipoContato tipoContato) {
        switch (tipoContato){
            case EMAIL -> { return usuarioDao.findByEmail(contato); }
            case CELULAR -> {return usuarioDao.findByCelular(contato); }
            default -> { throw new ServiceException("Tipo de contato inválido"); }
        }
    }

    @Override
    public Usuario buscarPorId(Long id) {
        return usuarioDao.findById(id).get();
    }
}
