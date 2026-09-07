package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.publicacao.PublicacaoUsuario;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.dto.BandaDTO;
import org.hibernate.collection.spi.PersistentBag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.baseapplication.core.utils.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioServiceImpl - montagem de DTOs fora de sessao Hibernate")
class UsuarioServiceImplBuscarSugestoesTest {

    @Mock
    private UsuarioDao usuarioDao;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    /**
     * Reproduz o cenário real: os usuários voltam do banco com a coleção LAZY
     * "publicacoes" não inicializada e sem sessão Hibernate ativa
     * (spring.jpa.open-in-view=false).
     */
    private Usuario usuarioComPublicacoesDesanexadas(Long id, String nome) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setPublicacoes(new PersistentBag<PublicacaoUsuario>(null));
        return usuario;
    }

    @Test
    @DisplayName("não deve tocar em coleções LAZY ao montar as sugestões")
    void naoDeveDispararLazyInitializationException() {
        when(usuarioDao.buscarSugestoes("ful"))
                .thenReturn(List.of(usuarioComPublicacoesDesanexadas(1L, "Fulano de Tal")));

        List<InfoPerfilUsuarioDTO> sugestoes = usuarioService.buscarSugestoes("ful");

        assertEquals(1, sugestoes.size());
        assertEquals(1L, sugestoes.get(0).getId());
        assertEquals("Fulano de Tal", sugestoes.get(0).getNome());
    }

    @Test
    @DisplayName("buscarPorEmail deve inicializar as publicacoes antes de montar o perfil")
    void buscarPorEmailNaoDeveDispararLazyInitializationException() {
        Usuario usuario = usuarioComPublicacoesDesanexadas(7L, "Fulano de Tal");
        usuario.setPublicacoes(List.of());
        when(usuarioDao.findByEmailWithPublicacoes("fulano@ezband.com"))
                .thenReturn(Optional.of(usuario));

        InfoPerfilUsuarioDTO perfil = usuarioService.buscarPorEmail("fulano@ezband.com");

        assertEquals(7L, perfil.getId());
        assertEquals("Fulano de Tal", perfil.getNome());
    }

    @Test
    @DisplayName("buscarInformacoesDoPerfil deve inicializar as publicacoes antes de montar o perfil")
    void buscarInformacoesDoPerfilNaoDeveDispararLazyInitializationException() {
        Usuario usuarioLogado = usuarioComPublicacoesDesanexadas(3L, "Fulano de Tal");

        Usuario usuarioComPublicacoesCarregadas = new Usuario();
        usuarioComPublicacoesCarregadas.setId(3L);
        usuarioComPublicacoesCarregadas.setNome("Fulano de Tal");
        usuarioComPublicacoesCarregadas.setPublicacoes(List.of());

        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioLogado);
            when(usuarioDao.findByIdWithPublicacoes(3L))
                    .thenReturn(Optional.of(usuarioComPublicacoesCarregadas));

            InfoPerfilUsuarioDTO perfil = usuarioService.buscarInformacoesDoPerfil();

            assertEquals(3L, perfil.getId());
            assertEquals("Fulano de Tal", perfil.getNome());
            assertTrue(perfil.getPublicacoes().isEmpty());
        }
    }

    @Test
    @DisplayName("buscarInformacoesDoPerfil deve lancar ResourceNotFoundException quando o usuario logado nao existe")
    void buscarInformacoesDoPerfilDeveLancarQuandoUsuarioNaoEncontrado() {
        Usuario usuarioLogado = new Usuario();
        usuarioLogado.setId(99L);

        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(usuarioLogado);
            when(usuarioDao.findByIdWithPublicacoes(99L)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class,
                    () -> usuarioService.buscarInformacoesDoPerfil());
        }
    }

    @Test
    @DisplayName("buscarBandasDoUsuario deve montar os BandaDTO do usuario logado")
    void buscarBandasDoUsuarioDeveMontarOsDtos() {
        Usuario membro = new Usuario();
        membro.setId(4L);
        membro.setNome("Membro Sem Nascimento");
        membro.setDataCriacao(LocalDate.of(2024, 5, 20));
        membro.setPublicacoes(new PersistentBag<PublicacaoUsuario>(null));

        Banda banda = new Banda();
        banda.setId(20L);
        banda.setNome("Banda Teste");
        banda.setShows(new ArrayList<>());
        banda.setEnsaios(new ArrayList<>());
        banda.setMembrosFantasma(new ArrayList<>());
        banda.setPublicacoes(new ArrayList<>());

        MusicoBanda musico = new MusicoBanda();
        musico.setUsuario(membro);
        musico.setBanda(banda);
        banda.setMusicos(List.of(musico));

        Usuario logado = new Usuario();
        logado.setId(4L);
        logado.setMusicoBandaList(List.of(musico));

        try (MockedStatic<Context> contextMock = mockStatic(Context.class)) {
            contextMock.when(Context::getUsuarioLogado).thenReturn(logado);

            List<BandaDTO> bandas = usuarioService.buscarBandasDoUsuario();

            assertEquals(1, bandas.size());
            assertEquals("Banda Teste", bandas.get(0).getNome());
            assertEquals(1, bandas.get(0).getMembros().size());
            assertNull(bandas.get(0).getMembros().get(0).getNascimento());
        }
    }
}
