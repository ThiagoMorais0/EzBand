package com.baseapplication.core.controller;

import com.baseapplication.core.dto.BuscaEstudioDTO;
import com.baseapplication.core.dto.CadastroEstudioDTO;
import com.baseapplication.core.dto.EstudioDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.Estudio;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.service.EstudioService;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EstudioController")
class EstudioControllerTest {

    @Mock
    private EstudioService estudioService;

    @InjectMocks
    private EstudioController estudioController;

    private CadastroEstudioDTO cadastroEstudioDTO;
    private EstudioDTO estudioDTO;
    private Estudio estudio;

    @BeforeEach
    void setUp() {
        cadastroEstudioDTO = new CadastroEstudioDTO();
        estudioDTO = new EstudioDTO();
        estudio = new Estudio();
    }

    @Test
    @DisplayName("Deve cadastrar estúdio com sucesso")
    void deveCadastrarEstudioComSucesso() {
//        when(cadastroEstudioDTO.toEntity()).thenReturn(estudio);
        when(estudioService.cadastrar(any(Estudio.class))).thenReturn(new Estudio());

        ResponseEntity resposta = estudioController.cadastrarEstudio(cadastroEstudioDTO);

        assertTrue(resposta.getStatusCode().equals(HttpStatus.OK));
        verify(estudioService, times(1)).cadastrar(any(Estudio.class));
    }

    @Test
    @DisplayName("Deve editar estúdio com sucesso")
    void deveEditarEstudioComSucesso() {
        doNothing().when(estudioService).editar(any(EstudioDTO.class));

        ResponseEntity resposta = estudioController.editar(estudioDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(estudioService, times(1)).editar(estudioDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao editar estúdio com exceção")
    void deveRetornarErroAoEditarEstudioComExcecao() {
        doThrow(new RuntimeException("Erro ao editar")).when(estudioService).editar(any(EstudioDTO.class));

        ResponseEntity resposta = estudioController.editar(estudioDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar estúdios do usuário com sucesso")
    void deveBuscarEstudiosDoUsuarioComSucesso() {
        List<Estudio> estudios = new ArrayList<>();
        when(estudioService.buscarEstudiosDoUsuario()).thenReturn(estudios);

        ResponseEntity resposta = estudioController.buscarEstudiosDoUsuario();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(estudioService, times(1)).buscarEstudiosDoUsuario();
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar estúdios do usuário com exceção")
    void deveRetornarErroAoBuscarEstudiosDoUsuarioComExcecao() {
        when(estudioService.buscarEstudiosDoUsuario()).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = estudioController.buscarEstudiosDoUsuario();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar todos os estúdios com sucesso")
    void deveBuscarTodosEstudiosComSucesso() {
        List<Estudio> estudios = new ArrayList<>();
        when(estudioService.buscarTodos()).thenReturn(estudios);

        List<EstudioDTO> resultado = estudioController.buscarTodos();

        assertNotNull(resultado);
        verify(estudioService, times(1)).buscarTodos();
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar estúdio por ID com exceção")
    void deveRetornarErroAoBuscarEstudioPorIdComExcecao() {
        Long idEstudio = 1L;
        when(estudioService.buscarPorId(anyLong())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = estudioController.buscarPorId(idEstudio);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve deletar todos os estúdios com sucesso")
    void deveDeletarTodosEstudiosComSucesso() {
        doNothing().when(estudioService).deletarTodos();

        ResponseEntity resposta = estudioController.deletarTodos();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(estudioService, times(1)).deletarTodos();
    }

    @Test
    @DisplayName("Deve cadastrar estúdio com imagem com sucesso")
    void deveCadastrarEstudioComImagemComSucesso() {
        String estudioJson = "{\"nome\":\"Estudio Teste\"}";
        MultipartFile imagem = mock(MultipartFile.class);
        ResponseEntity respostaEsperada = ResponseEntity.ok().build();

        when(estudioService.cadastrarComImagem(any(CadastroEstudioDTO.class), any())).thenReturn(respostaEsperada);

        ResponseEntity resposta = estudioController.cadastrarUsuarioComImagem(estudioJson, imagem);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(estudioService, times(1)).cadastrarComImagem(any(CadastroEstudioDTO.class), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar estúdio com JSON inválido")
    void deveLancarExcecaoAoCadastrarEstudioComJsonInvalido() {
        String estudioJsonInvalido = "json-invalido";
        MultipartFile imagem = mock(MultipartFile.class);

        assertThrows(InternalException.class, () -> 
            estudioController.cadastrarUsuarioComImagem(estudioJsonInvalido, imagem));
    }

    @Test
    @DisplayName("Deve editar estúdio com imagem com sucesso")
    void deveEditarEstudioComImagemComSucesso() {
        String estudioJson = "{\"nome\":\"Estudio Editado\"}";
        MultipartFile imagem = mock(MultipartFile.class);
        doNothing().when(estudioService).editarComImagem(anyString(), any());

        ResponseEntity resposta = estudioController.editarComImagem(estudioJson, imagem);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(estudioService, times(1)).editarComImagem(estudioJson, imagem);
    }

    @Test
    @DisplayName("Deve buscar ensaios por estúdio com sucesso")
    void deveBuscarEnsaiosPorEstudioComSucesso() {
        Long idEstudio = 1L;
        List<EnsaioDTO> ensaios = new ArrayList<>();
        when(estudioService.buscarEnsaiosPorEstudio(anyLong())).thenReturn(ensaios);

        ResponseEntity resposta = estudioController.buscarEnsaiosPorEstudio(idEstudio);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(estudioService, times(1)).buscarEnsaiosPorEstudio(idEstudio);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar ensaios por estúdio com exceção")
    void deveRetornarErroAoBuscarEnsaiosPorEstudioComExcecao() {
        Long idEstudio = 1L;
        when(estudioService.buscarEnsaiosPorEstudio(anyLong())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = estudioController.buscarEnsaiosPorEstudio(idEstudio);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar sugestões de estúdios com sucesso")
    void deveBuscarSugestoesDeEstudiosComSucesso() {
        BuscaEstudioDTO buscaDTO = new BuscaEstudioDTO();
        List<Estudio> estudios = new ArrayList<>();
        when(estudioService.buscarSugestoes(any(BuscaEstudioDTO.class))).thenReturn(estudios);

        ResponseEntity resposta = estudioController.buscarSugestoes(buscaDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(estudioService, times(1)).buscarSugestoes(buscaDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar sugestões com exceção")
    void deveRetornarErroAoBuscarSugestoesComExcecao() {
        BuscaEstudioDTO buscaDTO = new BuscaEstudioDTO();
        when(estudioService.buscarSugestoes(any(BuscaEstudioDTO.class))).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = estudioController.buscarSugestoes(buscaDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }
}
