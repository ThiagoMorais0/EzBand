package com.baseapplication.core.service;

import com.baseapplication.core.dto.ConfirmacaoTokenDTO;
import com.baseapplication.core.dto.ValidacaoCelularRequestDTO;
import org.apache.coyote.BadRequestException;

import java.util.Map;

public interface ValidacaoCelularService {
    
    Map<String, Object> gerarEEnviarToken(ValidacaoCelularRequestDTO request) throws BadRequestException;

    Map<String, Object> validarToken(ConfirmacaoTokenDTO request) throws BadRequestException;

    Map<String, Object> verificarDisponibilidade();
}
