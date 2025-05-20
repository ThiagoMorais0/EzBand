package com.baseapplication.core.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioDTO;
import com.baseapplication.core.dto.LoginDTO;

public interface AuthenticationService {
	ResponseEntity<?> login(LoginDTO credenciais);

	void registrar(CadastroDTO cadastroDTO);

	Boolean isTokenValid(String token);

	InfoUsuarioDTO buscarInfoUsuario(String email);

	ResponseEntity<?> cadastrarUsuarioComImagem(CadastroUsuarioDTO usuario, MultipartFile imagem);

}
