package com.baseapplication.core.controller;

import com.baseapplication.core.dto.BuscaLocalEventoDTO;
import com.baseapplication.core.dto.CadastroLocalEventoDTO;
import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.model.LocalEvento;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.service.LocalEventoService;
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
@DisplayName("Testes do LocalEventoController")
class LocalEventoControllerTest {

    @Mock
    private LocalEventoService localEventoService;

    @InjectMocks
    private LocalEventoController localEventoController;

    private CadastroLocalEventoDTO cadastroLocalEventoDTO;
    private LocalEventoDTO localEventoDTO;
    private LocalEvento localEvento;

    @BeforeEach
    void setUp() {
        cadastroLocalEventoDTO = new CadastroLocalEventoDTO();
        localEventoDTO = new LocalEventoDTO();
        localEvento = new LocalEvento();
    }

    @Test
    @DisplayName("Deve cadastrar local de evento com imagem com sucesso")
    void deveCadastrarLocalEventoComImagemComSucesso() {
        String localEventoJson = "{\"nome\":\"Local Teste\"}";
        MultipartFile imagem = mock(MultipartFile.class);
        ResponseEntity respostaEsperada = ResponseEntity.ok().build();

        when(localEventoService.cadastrarComImagem(any(CadastroLocalEventoDTO.class), any()))
                .thenReturn(respostaEsperada);

        ResponseEntity resposta = localEventoController.cadastrarUsuarioComImagem(localEventoJson, imagem);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(localEventoService, times(1)).cadastrarComImagem(any(CadastroLocalEventoDTO.class), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar local de evento com JSON inválido")
    void deveLancarExcecaoAoCadastrarLocalEventoComJsonInvalido() {
        String localEventoJsonInvalido = "json-invalido";
        MultipartFile imagem = mock(MultipartFile.class);

        assertThrows(InternalException.class, () -> 
            localEventoController.cadastrarUsuarioComImagem(localEventoJsonInvalido, imagem));
    }

    @Test
    @DisplayName("Deve editar local de evento com sucesso")
    void deveEditarLocalEventoComSucesso() {
        doNothing().when(localEventoService).editar(any(LocalEventoDTO.class));

        ResponseEntity resposta = localEventoController.editar(localEventoDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(localEventoService, times(1)).editar(localEventoDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao editar local de evento com exceção")
    void deveRetornarErroAoEditarLocalEventoComExcecao() {
        doThrow(new RuntimeException("Erro ao editar")).when(localEventoService).editar(any(LocalEventoDTO.class));

        ResponseEntity resposta = localEventoController.editar(localEventoDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar locais de evento do usuário com sucesso")
    void deveBuscarLocaisEventoDoUsuarioComSucesso() {
        List<LocalEvento> locaisEvento = new ArrayList<>();
        when(localEventoService.buscarLocalEventosDoUsuario()).thenReturn(locaisEvento);

        ResponseEntity resposta = localEventoController.buscarLocalEventosDoUsuario();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(localEventoService, times(1)).buscarLocalEventosDoUsuario();
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar locais de evento do usuário com exceção")
    void deveRetornarErroAoBuscarLocaisEventoDoUsuarioComExcecao() {
        when(localEventoService.buscarLocalEventosDoUsuario()).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = localEventoController.buscarLocalEventosDoUsuario();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar todos os locais de evento com sucesso")
    void deveBuscarTodosLocaisEventoComSucesso() {
        List<LocalEvento> locaisEvento = new ArrayList<>();
        when(localEventoService.buscarTodos()).thenReturn(locaisEvento);

        List<LocalEventoDTO> resultado = localEventoController.buscarTodos();

        assertNotNull(resultado);
        verify(localEventoService, times(1)).buscarTodos();
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar local de evento por ID com exceção")
    void deveRetornarErroAoBuscarLocalEventoPorIdComExcecao() {
        Long idLocalEvento = 1L;
        when(localEventoService.buscarPorId(anyLong())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = localEventoController.buscarPorId(idLocalEvento);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve deletar todos os locais de evento com sucesso")
    void deveDeletarTodosLocaisEventoComSucesso() {
        doNothing().when(localEventoService).deletarTodos();

        ResponseEntity resposta = localEventoController.deletarTodos();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(localEventoService, times(1)).deletarTodos();
    }

    @Test
    @DisplayName("Deve buscar shows por local de evento com sucesso")
    void deveBuscarShowsPorLocalEventoComSucesso() {
        Long idLocalEvento = 1L;
        List<ShowDTO> shows = new ArrayList<>();
        when(localEventoService.buscarShowsPorLocalEvento(anyLong())).thenReturn(shows);

        ResponseEntity resposta = localEventoController.buscarShowsPorLocalEvento(idLocalEvento);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(localEventoService, times(1)).buscarShowsPorLocalEvento(idLocalEvento);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar shows por local de evento com exceção")
    void deveRetornarErroAoBuscarShowsPorLocalEventoComExcecao() {
        Long idLocalEvento = 1L;
        when(localEventoService.buscarShowsPorLocalEvento(anyLong())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = localEventoController.buscarShowsPorLocalEvento(idLocalEvento);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar sugestões de locais de evento com sucesso")
    void deveBuscarSugestoesDeLocaisEventoComSucesso() {
        BuscaLocalEventoDTO buscaDTO = new BuscaLocalEventoDTO();
        List<LocalEvento> locaisEvento = new ArrayList<>();
        when(localEventoService.buscarSugestoes(any(BuscaLocalEventoDTO.class))).thenReturn(locaisEvento);

        ResponseEntity resposta = localEventoController.buscarSugestoes(buscaDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(localEventoService, times(1)).buscarSugestoes(buscaDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar sugestões com exceção")
    void deveRetornarErroAoBuscarSugestoesComExcecao() {
        BuscaLocalEventoDTO buscaDTO = new BuscaLocalEventoDTO();
        when(localEventoService.buscarSugestoes(any(BuscaLocalEventoDTO.class)))
                .thenThrow(new RuntimeException("Erro"));

        ResponseEntity resposta = localEventoController.buscarSugestoes(buscaDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }
}
