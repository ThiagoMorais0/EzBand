package com.baseapplication.core.controller;

import com.baseapplication.core.dto.AvaliacaoDTO;
import com.baseapplication.core.enums.TipoAvaliacao;
import com.baseapplication.core.service.AvaliacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do AvaliacaoController")
class AvaliacaoControllerTest {

    @Mock
    private AvaliacaoService avaliacaoService;

    @InjectMocks
    private AvaliacaoController avaliacaoController;

    private AvaliacaoDTO avaliacaoDTO;

    @BeforeEach
    void setUp() {
        avaliacaoDTO = new AvaliacaoDTO();
    }

    @Test
    @DisplayName("Deve avaliar com sucesso")
    void deveAvaliarComSucesso() {
        doNothing().when(avaliacaoService).avaliar(any(AvaliacaoDTO.class));

        ResponseEntity<?> resposta = avaliacaoController.avaliar(avaliacaoDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(avaliacaoService, times(1)).avaliar(avaliacaoDTO);
    }

    @Test
    @DisplayName("Deve retornar erro interno ao avaliar com exceção")
    void deveRetornarErroInternoAoAvaliarComExcecao() {
        doThrow(new RuntimeException("Erro ao avaliar")).when(avaliacaoService).avaliar(any(AvaliacaoDTO.class));

        ResponseEntity<?> resposta = avaliacaoController.avaliar(avaliacaoDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(avaliacaoService, times(1)).avaliar(avaliacaoDTO);
    }

    @Test
    @DisplayName("Deve buscar média de avaliação com sucesso")
    void deveBuscarMediaAvaliacaoComSucesso() {
        Long id = 1L;
        TipoAvaliacao tipo = TipoAvaliacao.ESTUDIO;
        Double mediaEsperada = 4.5;

        when(avaliacaoService.buscarMedia(anyLong(), any(TipoAvaliacao.class))).thenReturn(mediaEsperada);

        ResponseEntity<?> resposta = avaliacaoController.buscarMediaAvaliacao(id, tipo);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(mediaEsperada, resposta.getBody());
        verify(avaliacaoService, times(1)).buscarMedia(id, tipo);
    }

    @Test
    @DisplayName("Deve retornar erro interno ao buscar média com exceção")
    void deveRetornarErroInternoAoBuscarMediaComExcecao() {
        Long id = 1L;
        TipoAvaliacao tipo = TipoAvaliacao.ESTUDIO;

        when(avaliacaoService.buscarMedia(anyLong(), any(TipoAvaliacao.class)))
                .thenThrow(new RuntimeException("Erro ao buscar média"));

        ResponseEntity<?> resposta = avaliacaoController.buscarMediaAvaliacao(id, tipo);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(avaliacaoService, times(1)).buscarMedia(id, tipo);
    }
}
