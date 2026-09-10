package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.BandaDao;
import com.baseapplication.core.dao.DenunciaDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.DenunciaDTO;
import com.baseapplication.core.enums.TipoAlvoDenuncia;
import com.baseapplication.core.exception.ConflictException;
import com.baseapplication.core.exception.InvalidParamException;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.exception.RestrictionException;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Denuncia;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.DenunciaService;
import com.baseapplication.core.utils.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DenunciaServiceImpl implements DenunciaService {

    private static final int TAMANHO_MAXIMO_DESCRICAO = 2000;

    @Autowired
    private DenunciaDao denunciaDao;

    @Autowired
    private UsuarioDao usuarioDao;

    @Autowired
    private BandaDao bandaDao;

    @Override
    @Transactional
    public void registrarDenuncia(DenunciaDTO dto) {
        if (dto.getTipoAlvo() == null) {
            throw new InvalidParamException("Informe o tipo de perfil denunciado.");
        }
        if (dto.getIdAlvo() == null) {
            throw new InvalidParamException("Informe o perfil denunciado.");
        }
        if (dto.getCategoria() == null) {
            throw new InvalidParamException("Selecione uma categoria para a denúncia.");
        }

        Usuario denunciante = Context.getUsuarioLogado();
        if (denunciante == null || denunciante.getId() == null) {
            throw new RestrictionException("Faça login para denunciar um perfil.");
        }

        Denuncia denuncia = new Denuncia(denunciante, dto.getTipoAlvo(), dto.getCategoria(), normalizarDescricao(dto.getDescricao()));

        if (TipoAlvoDenuncia.USUARIO.equals(dto.getTipoAlvo())) {
            denuncia.setUsuarioDenunciado(buscarUsuarioDenunciado(denunciante, dto.getIdAlvo()));
        } else {
            denuncia.setBandaDenunciada(buscarBandaDenunciada(denunciante, dto.getIdAlvo()));
        }

        denunciaDao.save(denuncia);
    }

    private Usuario buscarUsuarioDenunciado(Usuario denunciante, Long idAlvo) {
        if (idAlvo.equals(denunciante.getId())) {
            throw new RestrictionException("Você não pode denunciar o seu próprio perfil.");
        }

        Usuario denunciado = usuarioDao.findById(idAlvo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário denunciado não encontrado."));

        if (denunciaDao.existsByDenuncianteIdAndUsuarioDenunciadoId(denunciante.getId(), idAlvo)) {
            throw new ConflictException("Você já denunciou este perfil. Nossa equipe está analisando.");
        }

        return denunciado;
    }

    private Banda buscarBandaDenunciada(Usuario denunciante, Long idAlvo) {
        Banda denunciada = bandaDao.findById(idAlvo)
                .orElseThrow(() -> new ResourceNotFoundException("Banda denunciada não encontrada."));

        if (denunciaDao.existsByDenuncianteIdAndBandaDenunciadaId(denunciante.getId(), idAlvo)) {
            throw new ConflictException("Você já denunciou esta banda. Nossa equipe está analisando.");
        }

        return denunciada;
    }

    private String normalizarDescricao(String descricao) {
        if (descricao == null || descricao.trim().isEmpty()) {
            return null;
        }
        String texto = descricao.trim();
        return texto.length() > TAMANHO_MAXIMO_DESCRICAO ? texto.substring(0, TAMANHO_MAXIMO_DESCRICAO) : texto;
    }
}
