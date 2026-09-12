package com.baseapplication.core.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Contrato com {@code EzBand-Web/src/utils/chaveMusica.js}.
 *
 * <p>Os casos aqui sao os mesmos do arquivo .test.js do front, com os mesmos resultados
 * esperados escritos a mao. E de proposito: o VS e gravado com a chave calculada aqui e
 * reencontrado com a chave calculada la, entao uma divergencia entre os dois nao lanca erro
 * em lugar nenhum -- o audio so some da tela. Estes dois arquivos sao o unico lugar onde
 * essa quebra fica visivel.
 */
class ChaveMusicaTest {

    @Test
    @DisplayName("junta titulo e artista com barra vertical, sem acento")
    void juntaTituloEArtista() {
        assertEquals("evidencias|chitaozinho", ChaveMusica.de("Evidências", "Chitãozinho"));
    }

    @Test
    @DisplayName("ignora diferenca de caixa")
    void ignoraCaixa() {
        assertEquals(ChaveMusica.de("sultans of swing", "dire straits"),
                ChaveMusica.de("SULTANS OF SWING", "Dire Straits"));
    }

    @Test
    @DisplayName("trata pontuacao e o espaco em volta dela como o mesmo separador")
    void pontuacaoViraEspaco() {
        assertEquals(ChaveMusica.de("Rock & Roll", null), ChaveMusica.de("Rock&Roll", null));
        assertEquals(ChaveMusica.de("Sweet Child O Mine", null),
                ChaveMusica.de("Sweet Child O' Mine", null));
    }

    @Test
    @DisplayName("colapsa espacos repetidos e das pontas")
    void colapsaEspacos() {
        assertEquals("aquarela do brasil|ary barroso",
                ChaveMusica.de("  Aquarela   do   Brasil  ", " Ary Barroso "));
    }

    @Test
    @DisplayName("aceita artista ausente sem perder o lado do titulo")
    void artistaAusente() {
        assertEquals("asa branca|", ChaveMusica.de("Asa Branca", null));
        assertEquals("asa branca|", ChaveMusica.de("Asa Branca", ""));
    }

    @Test
    @DisplayName("mantem musicas homonimas de artistas diferentes separadas")
    void homonimasNaoColidem() {
        assertNotEquals(ChaveMusica.de("Crazy", "Aerosmith"),
                ChaveMusica.de("Crazy", "Gnarls Barkley"));
    }

    @Test
    @DisplayName("normaliza acento pre-composto e decomposto para a mesma chave")
    void formasUnicodeEquivalentes() {
        // O mesmo titulo digitado no Android e colado do macOS chega em formas diferentes.
        String precomposto = "Cora\u00e7\u00e3o";   // um code point por letra acentuada
        String decomposto = "Corac\u0327a\u0303o"; // letra + combining mark
        assertNotEquals(precomposto, decomposto);
        assertEquals(ChaveMusica.de(precomposto, null), ChaveMusica.de(decomposto, null));
    }

    @Test
    @DisplayName("titulo nulo nao estoura -- a validacao de titulo obrigatorio e do service")
    void tituloNulo() {
        assertEquals("|", ChaveMusica.de(null, null));
    }
}
