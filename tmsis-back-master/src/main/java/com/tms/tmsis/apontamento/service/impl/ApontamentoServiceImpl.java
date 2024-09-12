package com.tms.tmsis.apontamento.service.impl;

import com.tms.tmsis.apontamento.dao.ApontamentoDao;
import com.tms.tmsis.apontamento.dto.ApontamentoDTO;
import com.tms.tmsis.apontamento.enums.TipoApontamento;
import com.tms.tmsis.apontamento.model.Apontamento;
import com.tms.tmsis.apontamento.service.ApontamentoService;
import com.tms.tmsis.core.dto.RetornoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApontamentoServiceImpl implements ApontamentoService {

    @Autowired
    ApontamentoDao apontamentoDao;

    @Override
    public RetornoDTO registrarApontamento(ApontamentoDTO apontamentoDTO) {
        try{
            TipoApontamento tipoApontamento = apontamentoDao.buscarInicioOuTermino();
            if(tipoApontamento == null){
                tipoApontamento = TipoApontamento.INICIO;
            }else{
                tipoApontamento = tipoApontamento == TipoApontamento.INICIO ? TipoApontamento.TERMINO : TipoApontamento.INICIO;
            }
            Apontamento apontamento = ApontamentoDTO.novoApontamentoToEntity(apontamentoDTO);
            apontamento.setTipo(tipoApontamento);
            apontamentoDao.save(apontamento);
            return RetornoDTO.success(tipoApontamento.getDescricao());
        }catch (Exception e){
            return RetornoDTO.error("Erro ao registrar apontamento.");
        }
    }
}
