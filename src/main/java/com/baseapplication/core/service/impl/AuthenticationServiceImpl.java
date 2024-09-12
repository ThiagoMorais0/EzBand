package com.baseapplication.core.service.impl;

import com.baseapplication.core.config.Context;
import com.baseapplication.core.config.TokenService;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.AuthenticationService;
import com.baseapplication.core.service.UsuarioService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenService tokenService;

    @Override
    public ResponseEntity login(LoginDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.getUsername(), data.getPassword());
            Authentication auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

            if (auth.isAuthenticated()) {
                return ResponseEntity.ok(new LoginResponseDTO(token));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } catch (BadCredentialsException | LockedException | DisabledException e) {
            // Trate diferentes exceções de autenticação aqui, se necessário
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @Override
    public RetornoDTO registrar(CadastroDTO dados) {
        try{
            verificaUsuarioJaCadastrado(dados);
            dados.setSenha(criptografar(dados.getSenha()));
            usuarioService.salvar(new Usuario(dados));
            return RetornoDTO.success();
        } catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    private String criptografar(String texto){
        return new BCryptPasswordEncoder().encode(texto);
    }

    private void verificaUsuarioJaCadastrado(CadastroDTO dados){
        if(usuarioService.findByLogin(dados.getLogin()) != null)
            throw new ServiceException("Nome de usuário já cadastrado.");

        if(usuarioService.findByEmail(dados.getEmail()) != null)
            throw new ServiceException("Email já cadastrado.");
    }

    @Override
    public Boolean isTokenValid(String token) {
        return !tokenService.validarToken(token).equals("");
    }

    @Override
    public RetornoDTO buscarInfoUsuario(String username) {
        return RetornoDTO.success(InfoUsuarioDTO.toDTO(usuarioService.findByLogin(username)));
    }
}
