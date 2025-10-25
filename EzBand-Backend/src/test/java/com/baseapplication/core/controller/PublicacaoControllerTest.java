package com.baseapplication.core.controller;

import com.baseapplication.core.dto.NovaPublicacaoDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.PublicacaoService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do PublicacaoController")
class PublicacaoControllerTest {

    @Mock
    private PublicacaoService publicacaoService;

    @InjectMocks
    private PublicacaoController publicacaoController;

    private List<MultipartFile> imagens;

    @BeforeEach
    void setUp() {
        imagens = new ArrayList<>();
        imagens.add(mock(MultipartFile.class));
    }

    @Test
    @DisplayName("Deve publicar publicação com sucesso")
    void devePublicarPublicacaoComSucesso() {
        String publicacaoJson = "{\"texto\":\"Teste de publicação\"}";
        doNothing().when(publicacaoService).publicar(any(NovaPublicacaoDTO.class), anyList());

        ResponseEntity<String> resposta = publicacaoController.publicarPublicacao(publicacaoJson, imagens);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Publicacao publicada com sucesso", resposta.getBody());
        verify(publicacaoService, times(1)).publicar(any(NovaPublicacaoDTO.class), anyList());
    }

    @Test
    @DisplayName("Deve lançar exceção ao publicar com JSON inválido")
    void deveLancarExcecaoAoPublicarComJsonInvalido() {
        String publicacaoJsonInvalida = "json-invalido";

        assertThrows(InternalException.class, () -> 
            publicacaoController.publicarPublicacao(publicacaoJsonInvalida, imagens));
    }

    @Test
    @DisplayName("Deve editar texto da publicação com sucesso")
    void deveEditarTextoPublicacaoComSucesso() {
        Long idPublicacao = 1L;
        String texto = "Texto editado";
        doNothing().when(publicacaoService).editarTextoPublicacao(anyLong(), anyString());

        ResponseEntity<String> resposta = publicacaoController.editarTextoPublicacao(idPublicacao, texto);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Texto editado com sucesso", resposta.getBody());
        verify(publicacaoService, times(1)).editarTextoPublicacao(idPublicacao, texto);
    }

    @Test
    @DisplayName("Deve retornar erro ao editar texto com exceção")
    void deveRetornarErroAoEditarTextoComExcecao() {
        Long idPublicacao = 1L;
        String texto = "Texto editado";
        doThrow(new RuntimeException("Erro ao editar")).when(publicacaoService)
                .editarTextoPublicacao(anyLong(), anyString());

        ResponseEntity<String> resposta = publicacaoController.editarTextoPublicacao(idPublicacao, texto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(publicacaoService, times(1)).editarTextoPublicacao(idPublicacao, texto);
    }

    @Test
    @DisplayName("Deve excluir publicação com sucesso")
    void deveExcluirPublicacaoComSucesso() {
        Long idPublicacao = 1L;
        doNothing().when(publicacaoService).excluirPublicacao(anyLong());

        ResponseEntity<String> resposta = publicacaoController.excluirPublicacao(idPublicacao);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals("Publicacao excluida com sucesso", resposta.getBody());
        verify(publicacaoService, times(1)).excluirPublicacao(idPublicacao);
    }

    @Test
    @DisplayName("Deve retornar erro ao excluir publicação com exceção")
    void deveRetornarErroAoExcluirPublicacaoComExcecao() {
        Long idPublicacao = 1L;
        doThrow(new RuntimeException("Erro ao excluir")).when(publicacaoService).excluirPublicacao(anyLong());

        ResponseEntity<String> resposta = publicacaoController.excluirPublicacao(idPublicacao);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(publicacaoService, times(1)).excluirPublicacao(idPublicacao);
    }
}
