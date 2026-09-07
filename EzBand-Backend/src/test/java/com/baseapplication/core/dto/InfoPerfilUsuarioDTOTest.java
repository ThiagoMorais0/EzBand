package com.baseapplication.core.dto;

import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InfoPerfilUsuarioDTO - montagem a partir de MusicoBanda")
class InfoPerfilUsuarioDTOTest {

    private MusicoBanda musicoDoUsuario(Usuario usuario) {
        MusicoBanda musico = new MusicoBanda();
        musico.setUsuario(usuario);
        musico.setInstrumentos("Guitarra");
        return musico;
    }

    /**
     * Usuarios criados pelo login com Google nao recebem data de nascimento,
     * entao o membro de banda pode chegar aqui com as datas nulas.
     */
    @Test
    @DisplayName("nao deve quebrar quando o membro nao tem data de nascimento")
    void naoDeveQuebrarComDataDeNascimentoNula() {
        Usuario usuario = new Usuario();
        usuario.setId(5L);
        usuario.setNome("Fulano de Tal");
        usuario.setDataCriacao(LocalDate.of(2024, 3, 10));
        usuario.setDataNascimento(null);

        InfoPerfilUsuarioDTO dto = new InfoPerfilUsuarioDTO(musicoDoUsuario(usuario));

        assertEquals(5L, dto.getId());
        assertEquals("Fulano de Tal", dto.getNome());
        assertNull(dto.getNascimento());
        assertEquals("10/03/2024", dto.getDataCriacao());
        assertEquals("Guitarra", dto.getInstrumentos());
    }

    @Test
    @DisplayName("nao deve quebrar quando o membro nao tem data de criacao")
    void naoDeveQuebrarComDataDeCriacaoNula() {
        Usuario usuario = new Usuario();
        usuario.setId(6L);
        usuario.setNome("Ciclano");
        usuario.setDataCriacao(null);
        usuario.setDataNascimento(LocalDate.of(1990, 1, 2));

        InfoPerfilUsuarioDTO dto = new InfoPerfilUsuarioDTO(musicoDoUsuario(usuario));

        assertEquals("02/01/1990", dto.getNascimento());
        assertNull(dto.getDataCriacao());
    }

    @Test
    @DisplayName("deve converter as datas quando o membro as possui")
    void deveConverterAsDatasQuandoPresentes() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNome("Beltrano");
        usuario.setDataCriacao(LocalDate.of(2023, 12, 25));
        usuario.setDataNascimento(LocalDate.of(1985, 6, 15));

        InfoPerfilUsuarioDTO dto = new InfoPerfilUsuarioDTO(musicoDoUsuario(usuario));

        assertEquals("15/06/1985", dto.getNascimento());
        assertEquals("25/12/2023", dto.getDataCriacao());
    }
}
