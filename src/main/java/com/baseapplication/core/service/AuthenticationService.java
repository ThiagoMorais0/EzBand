package com.baseapplication.core.service;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.dto.LoginDTO;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity login(LoginDTO credenciais);

    RetornoDTO registrar(CadastroDTO cadastroDTO);


    Boolean isTokenValid(String token);

    RetornoDTO buscarInfoUsuario(String username);
}
