package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.RelacionamentoSeguidorDao;
import com.baseapplication.core.enums.StatusSeguidor;
import com.baseapplication.core.enums.TipoRelacionamento;
import com.baseapplication.core.model.RelacionamentoSeguidor;
import com.baseapplication.core.model.RelacionamentoSeguidorId;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.RelacionamentoSeguidorService;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelacionamentoSeguidorServiceImpl implements RelacionamentoSeguidorService {

    private final RelacionamentoSeguidorDao relacionamentoSeguidorDao;

    @Override
    public TipoRelacionamento buscarTipoRelacionamento(Long idUsuarioLogado, Long idUsuarioBuscado) {
        if (idUsuarioLogado.equals(idUsuarioBuscado)) {
            return TipoRelacionamento.PROPRIO;
        }

        boolean euSigoEle = relacionamentoSeguidorDao.existeRelacionamento(idUsuarioLogado, idUsuarioBuscado, StatusSeguidor.ACEITO);
        boolean eleMeSigue = relacionamentoSeguidorDao.existeRelacionamento(idUsuarioBuscado, idUsuarioLogado, StatusSeguidor.ACEITO);

        if (euSigoEle && eleMeSigue) {
            return TipoRelacionamento.AMIGO;
        } else if (euSigoEle) {
            return TipoRelacionamento.SEGUIDOR;
        } else if (eleMeSigue) {
            return TipoRelacionamento.SEGUIDO;
        } else {
            return TipoRelacionamento.NENHUM;
        }
    }

    @Override
    public void seguirUsuario(Long idUsuarioASeguir) {
        Long idUsuarioLogado = Context.getUsuarioLogado().getId();
        
        RelacionamentoSeguidorId id = new RelacionamentoSeguidorId();
        id.setIdSeguidor(idUsuarioLogado);
        id.setIdSeguido(idUsuarioASeguir);

        RelacionamentoSeguidor relacionamento = new RelacionamentoSeguidor();
        relacionamento.setId(id);
        relacionamento.setStatus(StatusSeguidor.ACEITO);
        relacionamento.setDataSolicitacao(LocalDate.now());
        relacionamento.setDataAceitacao(LocalDate.now());

        relacionamentoSeguidorDao.save(relacionamento);
    }

    @Override
    public void deixarDeSeguir(Long idUsuario) {
        Long idUsuarioLogado = Context.getUsuarioLogado().getId();
        
        RelacionamentoSeguidorId id = new RelacionamentoSeguidorId();
        id.setIdSeguidor(idUsuarioLogado);
        id.setIdSeguido(idUsuario);

        relacionamentoSeguidorDao.deleteById(id);
    }

    @Override
    public List<Usuario> buscarAmigos() {
        return relacionamentoSeguidorDao.buscarAmigos(Context.getUsuarioLogado().getId());
    }

    @Override
    public List<Usuario> buscarSeguidores(Long idUsuario) {
        return relacionamentoSeguidorDao.buscarSeguidores(idUsuario, StatusSeguidor.ACEITO);
    }

    @Override
    public Long contarSeguidores(Long idUsuario) {
        return relacionamentoSeguidorDao.contarSeguidores(idUsuario, StatusSeguidor.ACEITO);
    }

    @Override
    public Long contarSeguindo(Long idUsuario) {
        return relacionamentoSeguidorDao.contarSeguindo(idUsuario, StatusSeguidor.ACEITO);
    }
}
