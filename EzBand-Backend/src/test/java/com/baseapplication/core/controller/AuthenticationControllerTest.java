package com.baseapplication.core.controller;

import com.baseapplication.core.dto.CadastroDTO;
import com.baseapplication.core.dto.CadastroUsuarioDTO;
import com.baseapplication.core.dto.InfoUsuarioDTO;
import com.baseapplication.core.dto.LoginDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AuthenticationController")
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    private LoginDTO loginDTO;
    private CadastroDTO cadastroDTO;
    private InfoUsuarioDTO infoUsuarioDTO;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        cadastroDTO = new CadastroDTO();
        infoUsuarioDTO = new InfoUsuarioDTO();
    }

    @Test
    @DisplayName("Deve realizar login com sucesso")
    void deveRealizarLoginComSucesso() {
        ResponseEntity respostaEsperada = ResponseEntity.ok("token");
        when(authenticationService.login(any(LoginDTO.class))).thenReturn(respostaEsperada);

        ResponseEntity<?> resposta = authenticationController.login(loginDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(authenticationService, times(1)).login(loginDTO);
    }

    @Test
    @DisplayName("Deve registrar usuário com sucesso")
    void deveRegistrarUsuarioComSucesso() {
        doNothing().when(authenticationService).registrar(any(CadastroDTO.class));

        assertDoesNotThrow(() -> authenticationController.registrar(cadastroDTO));
        verify(authenticationService, times(1)).registrar(cadastroDTO);
    }

    @Test
    @DisplayName("Deve cadastrar usuário com imagem com sucesso")
    void deveCadastrarUsuarioComImagemComSucesso() {
        String usuarioJson = "{\"nome\":\"Teste\",\"email\":\"teste@teste.com\"}";
        MultipartFile imagem = mock(MultipartFile.class);
        ResponseEntity respostaEsperada = ResponseEntity.ok().build();

        when(authenticationService.cadastrarUsuarioComImagem(any(CadastroUsuarioDTO.class), any()))
                .thenReturn(respostaEsperada);

        ResponseEntity<?> resposta = authenticationController.cadastrarUsuarioComImagem(usuarioJson, imagem);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(authenticationService, times(1)).cadastrarUsuarioComImagem(any(CadastroUsuarioDTO.class), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar usuário com JSON inválido")
    void deveLancarExcecaoAoCadastrarUsuarioComJsonInvalido() {
        String usuarioJsonInvalido = "json-invalido";
        MultipartFile imagem = mock(MultipartFile.class);

        assertThrows(InternalException.class, () -> 
            authenticationController.cadastrarUsuarioComImagem(usuarioJsonInvalido, imagem));
    }

    @Test
    @DisplayName("Deve validar token com sucesso")
    void deveValidarTokenComSucesso() {
        String token = "token-valido";
        when(authenticationService.isTokenValid(anyString())).thenReturn(true);

        boolean resultado = authenticationController.validarToken(token);

        assertTrue(resultado);
        verify(authenticationService, times(1)).isTokenValid(token);
    }

    @Test
    @DisplayName("Deve retornar false para token inválido")
    void deveRetornarFalseParaTokenInvalido() {
        String token = "token-invalido";
        when(authenticationService.isTokenValid(anyString())).thenReturn(false);

        boolean resultado = authenticationController.validarToken(token);

        assertFalse(resultado);
        verify(authenticationService, times(1)).isTokenValid(token);
    }

    @Test
    @DisplayName("Deve buscar informações do usuário com sucesso")
    void deveBuscarInformacoesDoUsuarioComSucesso() {
        String email = "teste@teste.com";
        when(authenticationService.buscarInfoUsuario(anyString())).thenReturn(infoUsuarioDTO);

        InfoUsuarioDTO resultado = authenticationController.buscarInfoUsuario(email);

        assertNotNull(resultado);
        verify(authenticationService, times(1)).buscarInfoUsuario(email);
    }
}
