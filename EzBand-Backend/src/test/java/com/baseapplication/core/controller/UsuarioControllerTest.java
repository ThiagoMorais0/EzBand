package com.baseapplication.core.controller;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.service.UsuarioService;
import com.baseapplication.core.utils.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do UsuarioController")
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuarioMock;
    private InfoUsuarioPainelDTO infoUsuarioPainelDTO;
    private InfoPerfilUsuarioDTO infoPerfilUsuarioDTO;
    private EmailDTO emailDTO;

    @BeforeEach
    void setUp() {
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);

        infoUsuarioPainelDTO = new InfoUsuarioPainelDTO();
        infoPerfilUsuarioDTO = new InfoPerfilUsuarioDTO();
        emailDTO = new EmailDTO();
    }

    @Test
    @DisplayName("Deve buscar informações do usuário para o painel com sucesso")
    void deveBuscarInfoUsuarioPainelComSucesso() {
        when(usuarioService.buscarInfoPainel()).thenReturn(infoUsuarioPainelDTO);

        InfoUsuarioPainelDTO resultado = usuarioController.buscarInfoUsuarioPainel();

        assertNotNull(resultado);
        verify(usuarioService, times(1)).buscarInfoPainel();
    }

    @Test
    @DisplayName("Deve buscar informações do perfil com sucesso")
    void deveBuscarInformacoesDoPerfilComSucesso() {
        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioMock);

            InfoPerfilUsuarioDTO resultado = usuarioController.buscarInformacoesDoPerfil();

            assertNotNull(resultado);
        }
    }

    @Test
    @DisplayName("Deve atualizar informações do perfil com sucesso")
    void deveAtualizarInformacoesDoPerfilComSucesso() {
        doNothing().when(usuarioService).atualizarInformacoesPerfil(any(InfoPerfilUsuarioDTO.class));

        assertDoesNotThrow(() -> usuarioController.atualizarInformacoesPerfil(infoPerfilUsuarioDTO));
        verify(usuarioService, times(1)).atualizarInformacoesPerfil(infoPerfilUsuarioDTO);
    }

    @Test
    @DisplayName("Deve editar usuário com imagem com sucesso")
    void deveEditarUsuarioComImagemComSucesso() {
        String usuarioJson = "{\"nome\":\"Usuario Teste\"}";
        MultipartFile imagem = mock(MultipartFile.class);
        Boolean removerImagemDePerfil = false;
        InfoPerfilUsuarioDTO dtoRetorno = new InfoPerfilUsuarioDTO();

        when(usuarioService.editarUsuarioComImagem(anyString(), any(), anyBoolean())).thenReturn(dtoRetorno);

        ResponseEntity resposta = usuarioController.editarUsuarioComImagem(usuarioJson, imagem, removerImagemDePerfil);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(usuarioService, times(1)).editarUsuarioComImagem(usuarioJson, imagem, removerImagemDePerfil);
    }

    @Test
    @DisplayName("Deve retornar erro ao editar usuário com exceção")
    void deveRetornarErroAoEditarUsuarioComExcecao() {
        String usuarioJson = "{\"nome\":\"Usuario Teste\"}";
        MultipartFile imagem = mock(MultipartFile.class);
        Boolean removerImagemDePerfil = false;

        when(usuarioService.editarUsuarioComImagem(anyString(), any(), anyBoolean()))
                .thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = usuarioController.editarUsuarioComImagem(usuarioJson, imagem, removerImagemDePerfil);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve verificar se email já está cadastrado")
    void deveVerificarSeEmailJaCadastrado() {
        String email = "teste@teste.com";
        when(usuarioService.verificarEmailJaCadastrado(anyString())).thenReturn(true);

        boolean resultado = usuarioController.verificarEmailJaCadastrado(email);

        assertTrue(resultado);
        verify(usuarioService, times(1)).verificarEmailJaCadastrado(email);
    }

    @Test
    @DisplayName("Deve retornar zero para quantidade de notificações")
    void deveRetornarZeroParaQuantidadeNotificacoes() {
        Integer resultado = usuarioController.buscarQuantidadeNotificacoes();

        assertEquals(0, resultado);
    }

    @Test
    @DisplayName("Deve enviar solicitação para ingressar na banda com sucesso")
    void deveEnviarSolicitacaoParaIngressarNaBandaComSucesso() {
        Long idBanda = 1L;
        String instrumento = "Guitarra";
        doNothing().when(usuarioService).enviarSolicitacaoParaIngressarBanda(anyLong(), anyString());

        ResponseEntity resposta = usuarioController.enviarSolicitacaoParaIngressarBanda(idBanda, instrumento);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(usuarioService, times(1)).enviarSolicitacaoParaIngressarBanda(idBanda, instrumento);
    }

    @Test
    @DisplayName("Deve retornar erro ao enviar solicitação com exceção")
    void deveRetornarErroAoEnviarSolicitacaoComExcecao() {
        Long idBanda = 1L;
        String instrumento = "Guitarra";
        doThrow(new RuntimeException("Erro ao enviar solicitação")).when(usuarioService)
                .enviarSolicitacaoParaIngressarBanda(anyLong(), anyString());

        ResponseEntity resposta = usuarioController.enviarSolicitacaoParaIngressarBanda(idBanda, instrumento);

        assertEquals(500, resposta.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve buscar informações do perfil por ID com sucesso")
    void deveBuscarInformacoesDoPerfilPorIdComSucesso() {
        Long idUsuario = 1L;
        when(usuarioService.buscarInformacoesDoPerfilPorId(anyLong())).thenReturn(infoPerfilUsuarioDTO);

        InfoPerfilUsuarioDTO resultado = usuarioController.buscarInformacoesDoPerfilPorId(idUsuario);

        assertNotNull(resultado);
        verify(usuarioService, times(1)).buscarInformacoesDoPerfilPorId(idUsuario);
    }

    @Test
    @DisplayName("Deve reportar erro com sucesso")
    void deveReportarErroComSucesso() {
        ReporteErroDTO dto = new ReporteErroDTO();
        dto.setMensagem("Erro encontrado");
        dto.setCategoria("Erro");
        doNothing().when(usuarioService).reportarErro(anyString());

        assertDoesNotThrow(() -> usuarioController.reportarErro(dto));
        verify(usuarioService, times(1)).reportarErro(dto.getCategoria() + ": " + dto.getMensagem());
    }

    @Test
    @DisplayName("Deve enviar email com sucesso")
    void deveEnviarEmailComSucesso() {
        doNothing().when(usuarioService).enviarEmail(any(EmailDTO.class));

        assertDoesNotThrow(() -> usuarioController.enviarEmail(emailDTO));
        verify(usuarioService, times(1)).enviarEmail(emailDTO);
    }

    @Test
    @DisplayName("Deve buscar próximos eventos do usuário com sucesso")
    void deveBuscarProximosEventosDoUsuarioComSucesso() {
        ResponseEntity respostaEsperada = ResponseEntity.ok().build();
        when(usuarioService.buscarProximosEventosDoUsuario()).thenReturn(respostaEsperada);

        ResponseEntity resposta = usuarioController.buscarProximosEventosDoUsuario();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(usuarioService, times(1)).buscarProximosEventosDoUsuario();
    }

    @Test
    @DisplayName("Deve buscar usuário por email com sucesso")
    void deveBuscarUsuarioPorEmailComSucesso() {
        String email = "teste@teste.com";
        when(usuarioService.buscarPorEmail(anyString())).thenReturn(new InfoPerfilUsuarioDTO());

        ResponseEntity resposta = usuarioController.buscarPorEmail(email);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(usuarioService, times(1)).buscarPorEmail(email);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar usuário por email com exceção")
    void deveRetornarErroAoBuscarUsuarioPorEmailComExcecao() {
        String email = "teste@teste.com";
        when(usuarioService.buscarPorEmail(anyString())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = usuarioController.buscarPorEmail(email);

        assertEquals(500, resposta.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve buscar participações especiais com sucesso")
    void deveBuscarParticipacoesEspeciaisComSucesso() {
        ParticipacoesEspeciaisDTO participacoes = new ParticipacoesEspeciaisDTO();
        when(usuarioService.buscarParticipacoesEspeciais()).thenReturn(participacoes);

        ResponseEntity resposta = usuarioController.buscarParticipacoesEspeciais();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(usuarioService, times(1)).buscarParticipacoesEspeciais();
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar participações especiais com exceção")
    void deveRetornarErroAoBuscarParticipacoesEspeciaisComExcecao() {
        when(usuarioService.buscarParticipacoesEspeciais()).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = usuarioController.buscarParticipacoesEspeciais();

        assertEquals(500, resposta.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve buscar globalmente com sucesso")
    void deveBuscarGlobalmenteComSucesso() {
        String termo = "teste";
        List<BuscaGlobalDTO> resultados = new ArrayList<>();
        when(usuarioService.buscarGlobal(anyString())).thenReturn(resultados);

        ResponseEntity<List<BuscaGlobalDTO>> resposta = usuarioController.buscarGlobal(termo);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(usuarioService, times(1)).buscarGlobal(termo);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar globalmente com exceção")
    void deveRetornarErroAoBuscarGlobalmenteComExcecao() {
        String termo = "teste";
        when(usuarioService.buscarGlobal(anyString())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<List<BuscaGlobalDTO>> resposta = usuarioController.buscarGlobal(termo);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar bandas do usuário com sucesso")
    void deveBuscarBandasDoUsuarioComSucesso() {
        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioMock);

            ResponseEntity resposta = usuarioController.buscarBandasDoUsuario();

            assertEquals(HttpStatus.OK, resposta.getStatusCode());
        }
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar bandas do usuário com exceção")
    void deveRetornarErroAoBuscarBandasDoUsuarioComExcecao() {
        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenThrow(new RuntimeException("Erro"));

            ResponseEntity resposta = usuarioController.buscarBandasDoUsuario();

            assertEquals(500, resposta.getStatusCodeValue());
        }
    }

    @Test
    @DisplayName("Deve seguir usuário com sucesso")
    void deveSeguirUsuarioComSucesso() {
        Long idUsuario = 2L;
        doNothing().when(usuarioService).seguirUsuario(anyLong());

        ResponseEntity resposta = usuarioController.seguirUsuario(idUsuario);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Usuário seguido com sucesso", resposta.getBody());
        verify(usuarioService, times(1)).seguirUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deve retornar erro ao seguir usuário com exceção")
    void deveRetornarErroAoSeguirUsuarioComExcecao() {
        Long idUsuario = 2L;
        doThrow(new RuntimeException("Erro ao seguir")).when(usuarioService).seguirUsuario(anyLong());

        ResponseEntity resposta = usuarioController.seguirUsuario(idUsuario);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(usuarioService, times(1)).seguirUsuario(idUsuario);
    }

    @Test
    @DisplayName("Deve deixar de seguir usuário com sucesso")
    void deveDeixarDeSeguirUsuarioComSucesso() {
        Long idUsuario = 2L;
        doNothing().when(usuarioService).deixarDeSeguir(anyLong());

        ResponseEntity resposta = usuarioController.deixarDeSeguir(idUsuario);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Deixou de seguir o usuário", resposta.getBody());
        verify(usuarioService, times(1)).deixarDeSeguir(idUsuario);
    }

    @Test
    @DisplayName("Deve retornar erro ao deixar de seguir usuário com exceção")
    void deveRetornarErroAoDeixarDeSeguirUsuarioComExcecao() {
        Long idUsuario = 2L;
        doThrow(new RuntimeException("Erro ao deixar de seguir")).when(usuarioService).deixarDeSeguir(anyLong());

        ResponseEntity resposta = usuarioController.deixarDeSeguir(idUsuario);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(usuarioService, times(1)).deixarDeSeguir(idUsuario);
    }
}
