package com.baseapplication.core.controller;

import com.baseapplication.core.service.SpotifyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do SpotifyController")
class SpotifyControllerTest {

    @Mock
    private SpotifyService spotifyService;

    @InjectMocks
    private SpotifyController spotifyController;

    @Test
    @DisplayName("Deve adicionar músicas da playlist ao repertório da banda com sucesso")
    void deveAdicionarMusicasDaPlaylistAoRepertorioDaBandaComSucesso() {
        String url = "https://open.spotify.com/playlist/123";
        Long idBanda = 1L;
        String resultadoEsperado = "teste";

        when(spotifyService.adicionarMusicasDaPlaylistAoRepertorioDaBanda(anyString(), anyLong()))
                .thenReturn(resultadoEsperado);

        ResponseEntity<?> resposta = spotifyController.getPlaylistInfo(url, idBanda);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertEquals(resultadoEsperado, resposta.getBody());
        verify(spotifyService, times(1)).adicionarMusicasDaPlaylistAoRepertorioDaBanda(url, idBanda);
    }

    @Test
    @DisplayName("Deve retornar erro ao adicionar músicas com exceção")
    void deveRetornarErroAoAdicionarMusicasComExcecao() {
        String url = "https://open.spotify.com/playlist/123";
        Long idBanda = 1L;

        when(spotifyService.adicionarMusicasDaPlaylistAoRepertorioDaBanda(anyString(), anyLong()))
                .thenThrow(new RuntimeException("Erro ao buscar playlist"));

        ResponseEntity<?> resposta = spotifyController.getPlaylistInfo(url, idBanda);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        assertEquals("Erro ao buscar dados da playlist.", resposta.getBody());
        verify(spotifyService, times(1)).adicionarMusicasDaPlaylistAoRepertorioDaBanda(url, idBanda);
    }
}
