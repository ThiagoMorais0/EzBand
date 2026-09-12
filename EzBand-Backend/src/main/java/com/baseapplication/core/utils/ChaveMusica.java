package com.baseapplication.core.utils;

import java.text.Normalizer;

/**
 * Identidade estavel de uma musica dentro de uma banda, derivada de titulo + artista.
 *
 * <p>Existe porque {@link com.baseapplication.core.model.embedded.Musica} e um
 * {@code @Embeddable} copiado por valor: a linha do repertorio do evento nao guarda o id
 * da linha do repertorio da banda de onde saiu, so os mesmos titulo e artista. Para o VS
 * carregado no repertorio da banda aparecer no setlist do evento, o vinculo tem que ser
 * essa identidade, nao uma FK.
 *
 * <p>A normalizacao e agressiva de proposito -- "Sweet Child O' Mine" e
 * "sweet child o mine" sao a mesma musica para quem digitou. Cada passo aqui tem um
 * espelho exato em {@code EzBand-Web/src/utils/chaveMusica.js}; mudar um lado sem o outro
 * faz o audio sumir da tela do palco sem erro nenhum.
 */
public final class ChaveMusica {

    private ChaveMusica() {
    }

    public static String de(String titulo, String artista) {
        return normalizar(titulo) + "|" + normalizar(artista);
    }

    private static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        // NFD separa a letra do acento; \p{M} remove so o acento, preservando a letra.
        String semAcento = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        // Fora do ASCII alfanumerico vira espaco em vez de sumir: sem isso "Rock&Roll"
        // colaria em "rockroll" e "Rock & Roll" viraria "rock roll" -- chaves diferentes
        // para o mesmo nome. Virando espaco, os dois colapsam no mesmo resultado.
        return semAcento.toLowerCase()
                .replaceAll("[^a-z0-9]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
