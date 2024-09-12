package com.tms.tmsis.core.service.impl;

import com.tms.tmsis.core.config.TokenService;
import com.tms.tmsis.core.model.Usuario;
import com.tms.tmsis.core.service.AuthenticationService;
import com.tms.tmsis.core.service.UsuarioService;
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
    public RetornoDTO registrar(CadastroDTO data) {
        try{
            if(usuarioService.findByLogin(data.getLogin()) != null)
                return RetornoDTO.error("Nome de usuário já cadastrado.");

            if(usuarioService.findByEmail(data.getEmail()) != null)
                return RetornoDTO.error("Email já cadastrado.");

            data.setSenha(new BCryptPasswordEncoder().encode(data.getSenha()));
            Usuario novoUsuario = new Usuario(data);
            usuarioService.salvar(novoUsuario);
            return RetornoDTO.success();
        } catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
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
