package com.baseapplication.core.service;

import com.baseapplication.core.model.Usuario;

public interface ReporteDeErroService {

    void reportarErro(String mensagem, Usuario usuario);
}
