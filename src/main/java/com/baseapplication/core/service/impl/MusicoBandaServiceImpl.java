package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.MusicoBandaDao;
import com.baseapplication.core.enums.PermissaoMusico;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.MusicoBandaId;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.MusicoBandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MusicoBandaServiceImpl implements MusicoBandaService {

    @Autowired
    private MusicoBandaDao musicoBandaDao;
    @Override
    public MusicoBanda buscarPorIdUsuarioEIdBanda(Long idUsuario, Long idBanda) {
        return musicoBandaDao.findById(new MusicoBandaId(idUsuario, idBanda)).orElseThrow();
    }

    @Override
    public MusicoBanda cadastrarUsuarioEmBanda(Long idUsuario, Long idBanda, String instrumentos) {
        MusicoBanda musicoBanda = new MusicoBanda();
        musicoBanda.setId(new MusicoBandaId(idUsuario, idBanda));
        musicoBanda.setInstrumentos(instrumentos);
        musicoBanda.setPermissao(PermissaoMusico.MEMBRO_REGULAR);
        return musicoBandaDao.save(musicoBanda);
    }

    @Override
    public void expulsar(Long idBanda, Long idUsuario) {
        musicoBandaDao.deleteById(new MusicoBandaId(idUsuario, idBanda));
    }

    @Override
    public List<Usuario> buscarMembrosPorIdBanda(Long idBanda) {
        return musicoBandaDao.buscarMembrosPorIdBanda(idBanda);
    }
}
