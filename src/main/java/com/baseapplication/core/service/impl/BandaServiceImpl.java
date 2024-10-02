package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.BandaDao;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.service.MusicoBandaService;
import com.google.protobuf.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BandaServiceImpl implements BandaService {

    @Autowired
    private BandaDao bandaDao;

    @Autowired
    private MusicoBandaService musicoBandaService;
    @Override
    public List<Banda> buscarBandasPorUsuario(Long idUsuario) {
        return bandaDao.buscarBandasPorUsuario(idUsuario).orElse(new ArrayList<Banda>());
    }

    @Override
    public List<Banda> buscarParticipacoesEspeciais(Long idUsuario) {
        return bandaDao.buscarParticipacoesEspeciais(idUsuario).orElse(new ArrayList<Banda>());
    }

    @Override
    public BandaDTO getInfo(Long idBanda) {
        return new BandaDTO(bandaDao.findById(idBanda).orElseThrow());
    }

    @Override
    public void cadastrarUsuario(Long idBanda, Long idUsuario, String instrumentos) {
        musicoBandaService.cadastrarUsuarioEmBanda(idUsuario, idBanda, instrumentos);
    }

    @Override
    public void expulsarUsuario(Long idBanda, Long idUsuario) {
        musicoBandaService.expulsar(idBanda, idUsuario);
    }

    @Override
    public List<InfoPerfilUsuarioDTO> buscarMembros(Long idBanda) {
        return musicoBandaService.buscarMembrosPorIdBanda(idBanda)
                .stream().map(InfoPerfilUsuarioDTO::new).collect(Collectors.toList());
    }
}
