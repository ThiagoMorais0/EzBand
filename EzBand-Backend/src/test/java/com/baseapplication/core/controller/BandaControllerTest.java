package com.baseapplication.core.controller;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.model.dto.EnsaioDTO;
import com.baseapplication.core.model.dto.ShowDTO;
import com.baseapplication.core.service.BandaService;
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
@DisplayName("Testes do BandaController")
class BandaControllerTest {

    @Mock
    private BandaService bandaService;

    @InjectMocks
    private BandaController bandaController;

    private BandaDTO bandaDTO;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        bandaDTO = new BandaDTO();
        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
    }

    @Test
    @DisplayName("Deve buscar informações da banda com sucesso")
    void deveBuscarInformacoesDaBandaComSucesso() {
        Long idBanda = 1L;
        when(bandaService.getInfo(anyLong())).thenReturn(bandaDTO);

        BandaDTO resultado = bandaController.getInfo(idBanda);

        assertNotNull(resultado);
        verify(bandaService, times(1)).getInfo(idBanda);
    }

    @Test
    @DisplayName("Deve buscar banda para ingressar com sucesso")
    void deveBuscarBandaParaIngressarComSucesso() {
        Long idBanda = 1L;
        ResponseEntity respostaEsperada = ResponseEntity.ok(bandaDTO);
        when(bandaService.buscarBandaParaIngressar(anyLong())).thenReturn(respostaEsperada);

        ResponseEntity<?> resposta = bandaController.buscarBandaParaIngressar(idBanda);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(bandaService, times(1)).buscarBandaParaIngressar(idBanda);
    }

    @Test
    @DisplayName("Deve criar nova banda com sucesso")
    void deveCriarNovaBandaComSucesso() {
        String bandaJson = "{\"nome\":\"Banda Teste\"}";
        MultipartFile logo = mock(MultipartFile.class);
        doNothing().when(bandaService).novaBanda(anyString(), any(MultipartFile.class));

        assertDoesNotThrow(() -> bandaController.novaBanda(bandaJson, logo));
        verify(bandaService, times(1)).novaBanda(bandaJson, logo);
    }

    @Test
    @DisplayName("Deve cadastrar usuário na banda com sucesso")
    void deveCadastrarUsuarioNaBandaComSucesso() {
        Long idBanda = 1L;
        Long idUsuario = 1L;
        String instrumentos = "Guitarra";
        doNothing().when(bandaService).cadastrarUsuario(anyLong(), anyLong(), anyString());

        assertDoesNotThrow(() -> bandaController.cadastrarUsuario(idBanda, idUsuario, instrumentos));
        verify(bandaService, times(1)).cadastrarUsuario(idBanda, idUsuario, instrumentos);
    }

    @Test
    @DisplayName("Deve expulsar usuário da banda com sucesso")
    void deveExpulsarUsuarioDaBandaComSucesso() {
        Long idBanda = 1L;
        Long idUsuario = 1L;
        doNothing().when(bandaService).expulsarUsuario(anyLong(), anyLong());

        assertDoesNotThrow(() -> bandaController.expulsarUsuario(idBanda, idUsuario));
        verify(bandaService, times(1)).expulsarUsuario(idBanda, idUsuario);
    }

    @Test
    @DisplayName("Deve buscar membros da banda com sucesso")
    void deveBuscarMembrosDaBandaComSucesso() {
        Long idBanda = 1L;
        List<InfoMembroBandaDTO> membros = new ArrayList<>();
        when(bandaService.buscarMembros(anyLong())).thenReturn(membros);

        List<InfoMembroBandaDTO> resultado = bandaController.getMembros(idBanda);

        assertNotNull(resultado);
        verify(bandaService, times(1)).buscarMembros(idBanda);
    }

    @Test
    @DisplayName("Deve buscar quantidade de shows com sucesso")
    void deveBuscarQuantidadeDeShowsComSucesso() {
        Long idBanda = 1L;
        Integer quantidade = 5;
        when(bandaService.buscarQuantidadeDeShows(anyLong())).thenReturn(quantidade);

        Integer resultado = bandaController.buscarQuantidadeDeShows(idBanda);

        assertEquals(quantidade, resultado);
        verify(bandaService, times(1)).buscarQuantidadeDeShows(idBanda);
    }

    @Test
    @DisplayName("Deve buscar quantidade de ensaios com sucesso")
    void deveBuscarQuantidadeDeEnsaiosComSucesso() {
        Long idBanda = 1L;
        Integer quantidade = 10;
        when(bandaService.buscarQuantidadeDeEnsaios(anyLong())).thenReturn(quantidade);

        Integer resultado = bandaController.buscarQuantidadeDeEnsaios(idBanda);

        assertEquals(quantidade, resultado);
        verify(bandaService, times(1)).buscarQuantidadeDeEnsaios(idBanda);
    }

    @Test
    @DisplayName("Deve buscar quantidade de notificações com sucesso")
    void deveBuscarQuantidadeDeNotificacoesComSucesso() {
        Long idBanda = 1L;
        Integer quantidade = 3;
        when(bandaService.buscarQuantidadeDeNotificacoes(anyLong())).thenReturn(quantidade);

        Integer resultado = bandaController.buscarQuantidadeDeNotificacoes(idBanda);

        assertEquals(quantidade, resultado);
        verify(bandaService, times(1)).buscarQuantidadeDeNotificacoes(idBanda);
    }

    @Test
    @DisplayName("Deve buscar quantidade de membros com sucesso")
    void deveBuscarQuantidadeDeMembrosComSucesso() {
        Long idBanda = 1L;
        Integer quantidade = 4;
        when(bandaService.buscarQuantidadeDeMembros(anyLong())).thenReturn(quantidade);

        Integer resultado = bandaController.buscarQuantidadeDeMembros(idBanda);

        assertEquals(quantidade, resultado);
        verify(bandaService, times(1)).buscarQuantidadeDeMembros(idBanda);
    }

    @Test
    @DisplayName("Deve buscar quantidade de músicas no repertório com sucesso")
    void deveBuscarQuantidadeDeMusicasNoRepertorioComSucesso() {
        Long idBanda = 1L;
        Integer quantidade = 20;
        when(bandaService.buscarQuantidadeDeMusicasNoRepertorio(anyLong())).thenReturn(quantidade);

        Integer resultado = bandaController.buscarQuantidadeDeMusicasNoRepertorio(idBanda);

        assertEquals(quantidade, resultado);
        verify(bandaService, times(1)).buscarQuantidadeDeMusicasNoRepertorio(idBanda);
    }

    @Test
    @DisplayName("Deve buscar permissões do músico com sucesso")
    void deveBuscarPermissoesDoMusicoComSucesso() {
        Long idBanda = 1L;
        
        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioMock);
            when(bandaService.getPermissoesMusico(anyLong(), anyLong())).thenReturn(new ArrayList<>());

            ResponseEntity<?> resposta = bandaController.getPermissoesMusico(idBanda);

            assertEquals(HttpStatus.OK, resposta.getStatusCode());
            verify(bandaService, times(1)).getPermissoesMusico(idBanda, usuarioMock.getId());
        }
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar permissões com exceção")
    void deveRetornarErroAoBuscarPermissoesComExcecao() {
        Long idBanda = 1L;
        
        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioMock);
            when(bandaService.getPermissoesMusico(anyLong(), anyLong()))
                    .thenThrow(new RuntimeException("Erro"));

            ResponseEntity<?> resposta = bandaController.getPermissoesMusico(idBanda);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
        }
    }

    @Test
    @DisplayName("Deve sair da banda com sucesso")
    void deveSairDaBandaComSucesso() {
        Long idBanda = 1L;
        
        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioMock);
            doNothing().when(bandaService).sairDaBanda(anyLong(), anyLong());

            assertDoesNotThrow(() -> bandaController.sairDaBanda(idBanda));
            verify(bandaService, times(1)).sairDaBanda(idBanda, usuarioMock.getId());
        }
    }

    @Test
    @DisplayName("Deve buscar shows futuros da banda com sucesso")
    void deveBuscarShowsFuturosDaBandaComSucesso() {
        Long idBanda = 1L;
        ShowsFuturosDTO showsFuturos = new ShowsFuturosDTO();
        
        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioMock);
            when(bandaService.buscarShowsFuturosBanda(anyLong(), anyLong())).thenReturn(showsFuturos);

            ShowsFuturosDTO resultado = bandaController.buscarShowsFuturosBanda(idBanda);

            assertNotNull(resultado);
            verify(bandaService, times(1)).buscarShowsFuturosBanda(idBanda, usuarioMock.getId());
        }
    }

    @Test
    @DisplayName("Deve buscar ensaios futuros da banda com sucesso")
    void deveBuscarEnsaiosFuturosDaBandaComSucesso() {
        Long idBanda = 1L;
        EnsaiosFuturosDTO ensaiosFuturos = new EnsaiosFuturosDTO();
        
        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioMock);
            when(bandaService.buscarEnsaiosFuturosBanda(anyLong(), anyLong())).thenReturn(ensaiosFuturos);

            EnsaiosFuturosDTO resultado = bandaController.buscarEnsaiosFuturosBanda(idBanda);

            assertNotNull(resultado);
            verify(bandaService, times(1)).buscarEnsaiosFuturosBanda(idBanda, usuarioMock.getId());
        }
    }

    @Test
    @DisplayName("Deve buscar ensaios com sucesso")
    void deveBuscarEnsaiosComSucesso() {
        Long idBanda = 1L;
        List<EnsaioDTO> ensaios = new ArrayList<>();
        when(bandaService.buscarEnsaios(anyLong())).thenReturn(ensaios);

        ResponseEntity<?> resposta = bandaController.buscarEnsaios(idBanda);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(bandaService, times(1)).buscarEnsaios(idBanda);
    }

    @Test
    @DisplayName("Deve retornar erro ao buscar ensaios com exceção")
    void deveRetornarErroAoBuscarEnsaiosComExcecao() {
        Long idBanda = 1L;
        when(bandaService.buscarEnsaios(anyLong())).thenThrow(new RuntimeException("Erro"));

        ResponseEntity<?> resposta = bandaController.buscarEnsaios(idBanda);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resposta.getStatusCode());
    }

    @Test
    @DisplayName("Deve buscar shows com sucesso")
    void deveBuscarShowsComSucesso() {
        Long idBanda = 1L;
        List<ShowDTO> shows = new ArrayList<>();
        when(bandaService.buscarShows(anyLong())).thenReturn(shows);

        ResponseEntity<?> resposta = bandaController.buscarShows(idBanda);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(bandaService, times(1)).buscarShows(idBanda);
    }

    @Test
    @DisplayName("Deve buscar repertório com sucesso")
    void deveBuscarRepertorioComSucesso() {
        Long idBanda = 1L;
        List<RepertorioBandaDTO> repertorio = new ArrayList<>();
        when(bandaService.buscarRepertorio(anyLong())).thenReturn(repertorio);

        List<RepertorioBandaDTO> resultado = bandaController.buscarRepertorio(idBanda);

        assertNotNull(resultado);
        verify(bandaService, times(1)).buscarRepertorio(idBanda);
    }

    @Test
    @DisplayName("Deve adicionar música ao repertório com sucesso")
    void deveAdicionarMusicaAoRepertorioComSucesso() {
        RepertorioBandaDTO repertorio = new RepertorioBandaDTO();
        doNothing().when(bandaService).adicionarMusicaAoRepertorio(any(RepertorioBandaDTO.class));

        assertDoesNotThrow(() -> bandaController.adicionarMusicaAoRepertorio(repertorio));
        verify(bandaService, times(1)).adicionarMusicaAoRepertorio(repertorio);
    }

    @Test
    @DisplayName("Deve atualizar música do repertório com sucesso")
    void deveAtualizarMusicaDoRepertorioComSucesso() {
        RepertorioBandaDTO repertorio = new RepertorioBandaDTO();
        doNothing().when(bandaService).atualizarMusicaRertorio(any(RepertorioBandaDTO.class));

        ResponseEntity<?> resposta = bandaController.atualizarMusicaRertorio(repertorio);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(bandaService, times(1)).atualizarMusicaRertorio(repertorio);
    }


    @Test
    @DisplayName("Deve enviar convite para usuário ingressar na banda com sucesso")
    void deveEnviarConviteParaUsuarioIngressarNaBandaComSucesso() {
        Long idBanda = 1L;
        Long idUsuarioConvidado = 2L;
        List<EventoConviteDTO> eventos = List.of(new EventoConviteDTO(10L, TipoEvento.SHOW));
        doNothing().when(bandaService).enviarConviteParaUsuarioIngressarBanda(anyLong(), anyLong(), anyList());

        ResponseEntity<?> resposta = bandaController.enviarConviteParaUsuarioIngressarBanda(idBanda, idUsuarioConvidado, eventos);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(bandaService, times(1)).enviarConviteParaUsuarioIngressarBanda(idBanda, idUsuarioConvidado, eventos);
    }

    @Test
    @DisplayName("Deve alterar permissão do membro com sucesso")
    void deveAlterarPermissaoDoMembroComSucesso() {
        EditarMembroMusicoBandaDTO permissaoDTO = new EditarMembroMusicoBandaDTO();
        doNothing().when(bandaService).alterarPermissaoMembro(any(EditarMembroMusicoBandaDTO.class));

        ResponseEntity<?> resposta = bandaController.alterarPermissaoMembro(permissaoDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(bandaService, times(1)).alterarPermissaoMembro(permissaoDTO);
    }

    @Test
    @DisplayName("Deve buscar sugestões com sucesso")
    void deveBuscarSugestoesComSucesso() {
        BuscaBandaDTO buscaDTO = new BuscaBandaDTO();
        List<Banda> bandas = new ArrayList<>();
        when(bandaService.buscarSugestoes(any(BuscaBandaDTO.class))).thenReturn(bandas);

        ResponseEntity<?> resposta = bandaController.buscarSugestoes(buscaDTO);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        verify(bandaService, times(1)).buscarSugestoes(buscaDTO);
    }
}
