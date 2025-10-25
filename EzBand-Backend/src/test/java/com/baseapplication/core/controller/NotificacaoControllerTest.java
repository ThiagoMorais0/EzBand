package com.baseapplication.core.controller;

import com.baseapplication.core.dto.NotificacaoDTO;
import com.baseapplication.core.enums.TipoParticipante;
import com.baseapplication.core.model.dto.RespostaNotificacaoDTO;
import com.baseapplication.core.model.superClasses.Notificacao;
import com.baseapplication.core.service.NotificacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do NotificacaoController")
class NotificacaoControllerTest {

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private NotificacaoController notificacaoController;

    private Notificacao notificacao;
    private RespostaNotificacaoDTO respostaNotificacaoDTO;

    @BeforeEach
    void setUp() {
        notificacao = mock(Notificacao.class);
        respostaNotificacaoDTO = new RespostaNotificacaoDTO();
    }

    @Test
    @DisplayName("Deve fazer stream de notificações com sucesso")
    void deveFazerStreamDeNotificacoesComSucesso() {
        Long destinatarioId = 1L;
        TipoParticipante destinatarioTipo = TipoParticipante.USUARIO;
        Flux<NotificacaoDTO> fluxoEsperado = Flux.empty();

        when(notificacaoService.streamNotificacoes(anyLong(), any(TipoParticipante.class)))
                .thenReturn(fluxoEsperado);

        Flux<NotificacaoDTO> resultado = notificacaoController.streamNotificacoes(destinatarioId, destinatarioTipo);

        assertNotNull(resultado);
        verify(notificacaoService, times(1)).streamNotificacoes(destinatarioId, destinatarioTipo);
    }

    @Test
    @DisplayName("Deve enviar notificação com sucesso")
    void deveEnviarNotificacaoComSucesso() {
        doNothing().when(notificacaoService).enviarNotificacaoSink(any(Notificacao.class));

        assertDoesNotThrow(() -> notificacaoController.enviarNotificacao(notificacao));
        verify(notificacaoService, times(1)).enviarNotificacaoSink(notificacao);
    }

    @Test
    @DisplayName("Deve deletar todas as notificações com sucesso")
    void deveDeletarTodasNotificacoesComSucesso() {
        doNothing().when(notificacaoService).deletarTodos();

        assertDoesNotThrow(() -> notificacaoController.deletarTodas());
        verify(notificacaoService, times(1)).deletarTodos();
    }

    @Test
    @DisplayName("Deve responder notificação com sucesso")
    void deveResponderNotificacaoComSucesso() {
        Long id = 1L;
        doNothing().when(notificacaoService).responderNotificacao(anyLong(), any(RespostaNotificacaoDTO.class));

        ResponseEntity<Void> resposta = notificacaoController.responder(id, respostaNotificacaoDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(notificacaoService, times(1)).responderNotificacao(id, respostaNotificacaoDTO);
    }

    @Test
    @DisplayName("Deve ler notificação com sucesso")
    void deveLerNotificacaoComSucesso() {
        Long id = 1L;
        doNothing().when(notificacaoService).lerNotificacao(anyLong());

        ResponseEntity<?> resposta = notificacaoController.lerNotificacao(id);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(notificacaoService, times(1)).lerNotificacao(id);
    }

    @Test
    @DisplayName("Deve retornar erro ao ler notificação com exceção")
    void deveRetornarErroAoLerNotificacaoComExcecao() {
        Long id = 1L;
        doThrow(new RuntimeException("Erro ao ler notificação")).when(notificacaoService).lerNotificacao(anyLong());

        ResponseEntity<?> resposta = notificacaoController.lerNotificacao(id);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        verify(notificacaoService, times(1)).lerNotificacao(id);
    }
}
