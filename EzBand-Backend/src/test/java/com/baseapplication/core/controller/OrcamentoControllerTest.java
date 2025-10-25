package com.baseapplication.core.controller;

import com.baseapplication.core.dto.NovoOrcamentoDTO;
import com.baseapplication.core.model.dto.ParametroCustoDTO;
import com.baseapplication.core.service.OrcamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do OrcamentoController")
class OrcamentoControllerTest {

    @Mock
    private OrcamentoService orcamentoService;

    @InjectMocks
    private OrcamentoController orcamentoController;

    private ParametroCustoDTO parametroCustoDTO;
    private NovoOrcamentoDTO novoOrcamentoDTO;
    private List<ParametroCustoDTO> listaParametros;

    @BeforeEach
    void setUp() {
        parametroCustoDTO = new ParametroCustoDTO();
        novoOrcamentoDTO = new NovoOrcamentoDTO();
        listaParametros = new ArrayList<>();
        listaParametros.add(parametroCustoDTO);
    }

    @Test
    @DisplayName("Deve buscar parâmetros de custo com sucesso")
    void deveBuscarParametrosCustoComSucesso() {
        Long idBanda = 1L;
        when(orcamentoService.buscarParametrosCusto(anyLong())).thenReturn(listaParametros);

        ResponseEntity<?> resposta = orcamentoController.buscarParametrosCusto(idBanda);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(orcamentoService, times(1)).buscarParametrosCusto(idBanda);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar parâmetros de custo com exceção")
    void deveRetornarErroAoBuscarParametrosCustoComExcecao() {
        Long idBanda = 1L;
        when(orcamentoService.buscarParametrosCusto(anyLong())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> resposta = orcamentoController.buscarParametrosCusto(idBanda);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(orcamentoService, times(1)).buscarParametrosCusto(idBanda);
    }

    @Test
    @DisplayName("Deve salvar parâmetros de custo com sucesso")
    void deveSalvarParametrosCustoComSucesso() {
        doNothing().when(orcamentoService).salvarParametrosCusto(anyList());

        ResponseEntity<?> resposta = orcamentoController.salvarParametrosCusto(listaParametros);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(orcamentoService, times(1)).salvarParametrosCusto(listaParametros);
    }

    @Test
    @DisplayName("Deve retornar erro ao salvar parâmetros de custo com exceção")
    void deveRetornarErroAoSalvarParametrosCustoComExcecao() {
        doThrow(new RuntimeException("Erro ao salvar")).when(orcamentoService).salvarParametrosCusto(anyList());

        ResponseEntity<?> resposta = orcamentoController.salvarParametrosCusto(listaParametros);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(orcamentoService, times(1)).salvarParametrosCusto(listaParametros);
    }

    @Test
    @DisplayName("Deve deletar parâmetro de custo com sucesso")
    void deveDeletarParametroCustoComSucesso() {
        doNothing().when(orcamentoService).deletarParametroCusto(any(ParametroCustoDTO.class));

        ResponseEntity<?> resposta = orcamentoController.deletarParametroCusto(parametroCustoDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(orcamentoService, times(1)).deletarParametroCusto(parametroCustoDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao deletar parâmetro de custo com exceção")
    void deveRetornarErroAoDeletarParametroCustoComExcecao() {
        doThrow(new RuntimeException("Erro ao deletar")).when(orcamentoService)
                .deletarParametroCusto(any(ParametroCustoDTO.class));

        ResponseEntity<?> resposta = orcamentoController.deletarParametroCusto(parametroCustoDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(orcamentoService, times(1)).deletarParametroCusto(parametroCustoDTO);
    }

    @Test
    @DisplayName("Deve gerar orçamento com sucesso")
    void deveGerarOrcamentoComSucesso() {
        doNothing().when(orcamentoService).gerarOrcamento(any(NovoOrcamentoDTO.class));

        ResponseEntity<?> resposta = orcamentoController.gerarOrcamento(novoOrcamentoDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(orcamentoService, times(1)).gerarOrcamento(novoOrcamentoDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao gerar orçamento com exceção")
    void deveRetornarErroAoGerarOrcamentoComExcecao() {
        doThrow(new RuntimeException("Erro ao gerar orçamento")).when(orcamentoService)
                .gerarOrcamento(any(NovoOrcamentoDTO.class));

        ResponseEntity<?> resposta = orcamentoController.gerarOrcamento(novoOrcamentoDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(orcamentoService, times(1)).gerarOrcamento(novoOrcamentoDTO);
    }
}
