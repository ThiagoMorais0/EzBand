package com.tms.tmsis.core.service;

import com.tms.tmsis.core.dto.CadastroDTO;
import com.tms.tmsis.core.dto.LoginDTO;
import com.tms.tmsis.core.dto.RetornoDTO;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity login(LoginDTO credenciais);

    RetornoDTO registrar(CadastroDTO cadastroDTO);


    Boolean isTokenValid(String token);

    RetornoDTO buscarInfoUsuario(String username);
}
