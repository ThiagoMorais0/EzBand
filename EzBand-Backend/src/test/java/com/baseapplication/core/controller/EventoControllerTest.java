package com.baseapplication.core.controller;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.dto.superClasses.EventoDTO;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.EventoService;
import com.baseapplication.core.utils.DateUtils;
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

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do EventoController")
class EventoControllerTest {

    @Mock
    private EventoService eventoService;

    @InjectMocks
    private EventoController eventoController;

    private Evento evento;
    private InformacoesEventoDTO informacoesEventoDTO;
    private MusicoEventoDTO musicoEventoDTO;
    private ConviteEventoDTO conviteEventoDTO;
    private NovoShowDTO novoShowDTO;
    private NovoEnsaioDTO novoEnsaioDTO;

    @BeforeEach
    void setUp() {
        evento = mock(Evento.class);
        informacoesEventoDTO = new InformacoesShowDTO();
        musicoEventoDTO = new MusicoEventoDTO();
        conviteEventoDTO = new ConviteEventoDTO();
        novoShowDTO = new NovoShowDTO();
        novoEnsaioDTO = new NovoEnsaioDTO();
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar evento por ID com exceção")
    void deveRetornarErroAoBuscarEventoPorIdComExcecao() {
        Long idEvento = 1L;
        TipoEvento tipoEvento = TipoEvento.SHOW;
        when(eventoService.buscarPorId(anyLong(), any(TipoEvento.class)))
                .thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> resposta = eventoController.buscar(idEvento, tipoEvento);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar informações do evento com sucesso")
    void deveBuscarInformacoesDoEventoComSucesso() {
        Long idEvento = 1L;
        TipoEvento tipoEvento = TipoEvento.SHOW;
        when(eventoService.buscarPobuscarInformacoesEventorId(anyLong(), any(TipoEvento.class)))
                .thenReturn(informacoesEventoDTO);

        InformacoesEventoDTO resultado = eventoController.buscarInformacoesEvento(idEvento, tipoEvento);

        assertNotNull(resultado);
        verify(eventoService, times(1)).buscarPobuscarInformacoesEventorId(idEvento, tipoEvento);
    }

    @Test
    @DisplayName("Deve atualizar informações do evento com sucesso")
    void deveAtualizarInformacoesDoEventoComSucesso() {
        doNothing().when(eventoService).atualizarInformacoesEvento(any(InformacoesEventoDTO.class));

        assertDoesNotThrow(() -> eventoController.atualizarInformacoesEvento(informacoesEventoDTO));
        verify(eventoService, times(1)).atualizarInformacoesEvento(informacoesEventoDTO);
    }

    @Test
    @DisplayName("Deve buscar músico para evento com sucesso")
    void deveBuscarMusicoParaEventoComSucesso() {
        String contato = "teste@teste.com";
        TipoContato tipoContato = TipoContato.EMAIL;
        when(eventoService.buscarMusicoParaEvento(anyString(), any(TipoContato.class)))
                .thenReturn(musicoEventoDTO);

        MusicoEventoDTO resultado = eventoController.buscarMusicoParaEvento(contato, tipoContato);

        assertNotNull(resultado);
        verify(eventoService, times(1)).buscarMusicoParaEvento(contato, tipoContato);
    }

    @Test
    @DisplayName("Deve enviar convite para evento com sucesso")
    void deveEnviarConviteParaEventoComSucesso() {
        doNothing().when(eventoService).enviarConviteParaEvento(any(ConviteEventoDTO.class));

        assertDoesNotThrow(() -> eventoController.enviarConviteParaEvento(conviteEventoDTO));
        verify(eventoService, times(1)).enviarConviteParaEvento(conviteEventoDTO);
    }

    @Test
    @DisplayName("Deve buscar repertório do evento com sucesso")
    void deveBuscarRepertorioDoEventoComSucesso() {
        Long idEvento = 1L;
        String tipoEvento = "SHOW";
        when(eventoService.buscarRepertorioEvento(anyLong(), any(TipoEvento.class))).thenReturn(new ArrayList<>());

        ResponseEntity<?> resposta = eventoController.buscarRepertorioEvento(idEvento, tipoEvento);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(eventoService, times(1)).buscarRepertorioEvento(idEvento, TipoEvento.SHOW);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar repertório com exceção")
    void deveRetornarErroAoBuscarRepertorioComExcecao() {
        Long idEvento = 1L;
        String tipoEvento = "SHOW";
        when(eventoService.buscarRepertorioEvento(anyLong(), any(TipoEvento.class)))
                .thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> resposta = eventoController.buscarRepertorioEvento(idEvento, tipoEvento);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve atualizar repertório do evento com sucesso")
    void deveAtualizarRepertorioDoEventoComSucesso() {
        AtualizacaoRepertorioEventoDTO atualizacaoRepertorio = new AtualizacaoRepertorioEventoDTO();
        doNothing().when(eventoService).atualizarRepertorioEvento(any(AtualizacaoRepertorioEventoDTO.class));

        ResponseEntity<?> resposta = eventoController.atualizarRepertorioEvento(atualizacaoRepertorio);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(eventoService, times(1)).atualizarRepertorioEvento(atualizacaoRepertorio);
    }

    @Test
    @DisplayName("Deve retornar erro ao atualizar repertório com exceção")
    void deveRetornarErroAoAtualizarRepertorioComExcecao() {
        AtualizacaoRepertorioEventoDTO atualizacaoRepertorio = new AtualizacaoRepertorioEventoDTO();
        doThrow(new RuntimeException("Erro")).when(eventoService)
                .atualizarRepertorioEvento(any(AtualizacaoRepertorioEventoDTO.class));

        ResponseEntity<?> resposta = eventoController.atualizarRepertorioEvento(atualizacaoRepertorio);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve atualizar música do repertório com sucesso")
    void deveAtualizarMusicaDoRepertorioComSucesso() {
        AtualizacaoMusicaRepertorioDTO atualizacaoMusica = new AtualizacaoMusicaRepertorioDTO();
        doNothing().when(eventoService).atualizarMusicaRepertorio(any(AtualizacaoMusicaRepertorioDTO.class));

        ResponseEntity<?> resposta = eventoController.atualizarMusicaRepertorio(atualizacaoMusica);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(eventoService, times(1)).atualizarMusicaRepertorio(atualizacaoMusica);
    }

    @Test
    @DisplayName("Deve buscar membros e disponibilidade para show com sucesso")
    void deveBuscarMembrosEDisponibilidadeParaShowComSucesso() {
        Long idBanda = 1L;
        String data = "2025-12-31";
        LocalDate localDate = LocalDate.of(2025, 12, 31);
        ResponseEntity respostaEsperada = ResponseEntity.ok().build();

        try (MockedStatic<DateUtils> dateUtilsMock = mockStatic(DateUtils.class)) {
            dateUtilsMock.when(() -> DateUtils.stringToLocalDate(anyString())).thenReturn(localDate);
            when(eventoService.buscarMembrosEDisponibilidadeParaShow(anyLong(), any(LocalDate.class)))
                    .thenReturn(respostaEsperada);

            ResponseEntity<?> resposta = eventoController.buscarMembrosParaShow(idBanda, data);

            assertEquals(HttpStatus.OK, resposta.getStatusCode());
            verify(eventoService, times(1)).buscarMembrosEDisponibilidadeParaShow(idBanda, localDate);
        }
    }

    @Test
    @DisplayName("Deve marcar show com sucesso")
    void deveMarcarShowComSucesso() {
        doNothing().when(eventoService).marcarShow(any(NovoShowDTO.class));

        ResponseEntity<?> resposta = eventoController.marcarShow(novoShowDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(eventoService, times(1)).marcarShow(novoShowDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao marcar show com exceção")
    void deveRetornarErroAoMarcarShowComExcecao() {
        doThrow(new RuntimeException("Erro")).when(eventoService).marcarShow(any(NovoShowDTO.class));

        ResponseEntity<?> resposta = eventoController.marcarShow(novoShowDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve marcar ensaio com sucesso")
    void deveMarcarEnsaioComSucesso() {
        doNothing().when(eventoService).marcarEnsaio(any(NovoEnsaioDTO.class));

        ResponseEntity<?> resposta = eventoController.marcarEnsaio(novoEnsaioDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(eventoService, times(1)).marcarEnsaio(novoEnsaioDTO);
    }

    @Test
    @DisplayName("Deve retornar erro ao marcar ensaio com exceção")
    void deveRetornarErroAoMarcarEnsaioComExcecao() {
        doThrow(new RuntimeException("Erro")).when(eventoService).marcarEnsaio(any(NovoEnsaioDTO.class));

        ResponseEntity<?> resposta = eventoController.marcarEnsaio(novoEnsaioDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve verificar se notificação do show foi aceita por todos os membros")
    void deveVerificarSeNotificacaoShowFoiAceitaPorTodosMembros() {
        Long idShow = 1L;
        when(eventoService.isNotificacaoShowAceitaPorTodosMembros(anyLong())).thenReturn(true);

        boolean resultado = eventoController.isNotificacaoShowAceitaPorTodosMembros(idShow);

        assertTrue(resultado);
        verify(eventoService, times(1)).isNotificacaoShowAceitaPorTodosMembros(idShow);
    }

    @Test
    @DisplayName("Deve aceitar notificação com sucesso")
    void deveAceitarNotificacaoComSucesso() {
        Long idNotificacao = 1L;
        doNothing().when(eventoService).aceitarNotificacao(anyLong());

        assertDoesNotThrow(() -> eventoController.aceitarNotificacao(idNotificacao));
        verify(eventoService, times(1)).aceitarNotificacao(idNotificacao);
    }

    @Test
    @DisplayName("Deve recusar notificação com sucesso")
    void deveRecusarNotificacaoComSucesso() {
        Long idNotificacao = 1L;
        doNothing().when(eventoService).recusarNotificacao(anyLong());

        assertDoesNotThrow(() -> eventoController.recusarNotificacao(idNotificacao));
        verify(eventoService, times(1)).recusarNotificacao(idNotificacao);
    }

    @Test
    @DisplayName("Deve cancelar evento com sucesso")
    void deveCancelarEventoComSucesso() {
        Long idEvento = 1L;
        TipoEvento tipoEvento = TipoEvento.SHOW;
        doNothing().when(eventoService).cancelarEvento(anyLong(), any(TipoEvento.class));

        ResponseEntity<?> resposta = eventoController.cancelarEvento(idEvento, tipoEvento);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(eventoService, times(1)).cancelarEvento(idEvento, tipoEvento);
    }

    @Test
    @DisplayName("Deve retornar erro ao cancelar evento com exceção")
    void deveRetornarErroAoCancelarEventoComExcecao() {
        Long idEvento = 1L;
        TipoEvento tipoEvento = TipoEvento.SHOW;
        doThrow(new RuntimeException("Erro")).when(eventoService).cancelarEvento(anyLong(), any(TipoEvento.class));

        ResponseEntity<?> resposta = eventoController.cancelarEvento(idEvento, tipoEvento);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }
}
