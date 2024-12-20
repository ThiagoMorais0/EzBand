package com.baseapplication.core.service.impl;

import com.baseapplication.core.config.TokenService;
import com.baseapplication.core.dto.*;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.service.AuthenticationService;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.UsuarioService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ImagemService imagemService;

    @Override
    public ResponseEntity login(LoginDTO data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.getEmail(), data.getPassword());
            Authentication auth = this.authenticationManager.authenticate(usernamePassword);

            var token = tokenService.gerarToken((Usuario) auth.getPrincipal());

            if (auth.isAuthenticated()) {
                Usuario usuario = usuarioService.findByEmail(data.getEmail());
                return ResponseEntity.ok(new LoginResponseDTO(token, usuario));
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
        try {
            verificaUsuarioJaCadastrado(dados);
            dados.setSenha(criptografar(dados.getSenha()));
            usuarioService.salvar(new Usuario(dados));
            return RetornoDTO.success();
        } catch (Exception e) {
            return RetornoDTO.error(e.getMessage());
        }
    }

    private String criptografar(String texto) {
        return new BCryptPasswordEncoder().encode(texto);
    }

    private void verificaUsuarioJaCadastrado(CadastroUsuarioDTO dados) {
        if (usuarioService.findByEmail(dados.getEmail()) != null)
            throw new ServiceException("Nome de usuário já cadastrado.");

        if (usuarioService.findByEmail(dados.getEmail()) != null)
            throw new ServiceException("Email já cadastrado.");
    }

    private void verificaUsuarioJaCadastrado(CadastroDTO dados) {
//        if (usuarioService.findByLogin(dados.getLogin()) != null)
//            throw new ServiceException("Nome de usuário já cadastrado.");

        if (usuarioService.findByEmail(dados.getEmail()) != null)
            throw new ServiceException("Email já cadastrado.");
    }

    @Override
    public Boolean isTokenValid(String token) {
        return !tokenService.validarToken(token).equals("");
    }

    @Override
    public RetornoDTO buscarInfoUsuario(String email) {
        return RetornoDTO.success(InfoUsuarioDTO.toDTO(usuarioService.findByEmail(email)));
    }

    @Override
    public RetornoDTO cadastrarUsuarioComImagem(CadastroUsuarioDTO usuario, MultipartFile imagem) {
        try {
            String urlImagem = imagemService.saveImageAndGetUrl(imagem);
            verificaUsuarioJaCadastrado(usuario);
            usuario.setSenha(criptografar(usuario.getSenha()));
            usuarioService.salvar(new Usuario(usuario, urlImagem));
            return RetornoDTO.success();
        } catch (Exception e) {
            return RetornoDTO.error(e.getMessage());
        }
    }
}
