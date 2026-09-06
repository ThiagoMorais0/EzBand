package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.dto.InfoPerfilUsuarioDTO;
import com.baseapplication.core.model.publicacao.PublicacaoUsuario;
import com.baseapplication.core.model.Usuario;
import org.hibernate.collection.spi.PersistentBag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
}
