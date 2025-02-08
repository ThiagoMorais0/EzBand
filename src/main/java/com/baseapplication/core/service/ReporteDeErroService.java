package com.baseapplication.core.service;

import com.baseapplication.core.controller.NovoEnsaioDTO;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.superClasses.Evento;

import java.time.LocalDate;
import java.util.List;

public interface ReporteDeErroService {

    void reportarErro(String mensagem, Usuario usuario);
}
