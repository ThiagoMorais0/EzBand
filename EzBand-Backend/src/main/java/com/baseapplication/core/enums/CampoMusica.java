package com.baseapplication.core.enums;

import java.sql.Time;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.baseapplication.core.model.embedded.Musica;

/**
 * Os campos de uma musica que existem tanto no repertorio da banda quanto no do evento.
 *
 * <p>Existe para a propagacao de uma edicao do setlist da banda para os shows pendentes: so o
 * que mudou de fato viaja, porque a linha do evento e uma copia que a banda pode ter ajustado
 * para aquele show -- meio tom abaixo, uma observacao de palco. Sobrescrever a linha inteira
 * apagaria esses ajustes sem aviso; copiar campo a campo mantem o que ninguem tocou.
 *
 * <p>Indice, bloco e tipo do item ficam de fora porque sao do evento, nao da musica. Posicao no
 * show, energia e relevancia idem, no sentido inverso: pertencem a banda e nao existem na linha
 * do evento.
 */
public enum CampoMusica {

    TITULO(Musica::getTitulo, (origem, destino) -> destino.setTitulo(origem.getTitulo())),
    ARTISTA(Musica::getArtista, (origem, destino) -> destino.setArtista(origem.getArtista())),
    DESCRICAO(Musica::getDescricao, (origem, destino) -> destino.setDescricao(origem.getDescricao())),
    OBSERVACAO(Musica::getObservacao, (origem, destino) -> destino.setObservacao(origem.getObservacao())),
    DURACAO(Musica::getDuracao, (origem, destino) -> destino.setDuracao(origem.getDuracao())),
    TONALIDADE(Musica::getTonalidade, (origem, destino) -> destino.setTonalidade(origem.getTonalidade())),
    URL_YOUTUBE(Musica::getUrlYoutube, (origem, destino) -> destino.setUrlYoutube(origem.getUrlYoutube())),
    URL_SPOTIFY(Musica::getUrlSpotify, (origem, destino) -> destino.setUrlSpotify(origem.getUrlSpotify())),
    LETRA(Musica::getLetra, (origem, destino) -> destino.setLetra(origem.getLetra())),
    BPM(Musica::getBpm, (origem, destino) -> destino.setBpm(origem.getBpm()));

    private final Function<Musica, Object> leitor;
    private final BiConsumer<Musica, Musica> copiador;

    CampoMusica(Function<Musica, Object> leitor, BiConsumer<Musica, Musica> copiador) {
        this.leitor = leitor;
        this.copiador = copiador;
    }

    public boolean mudou(Musica antes, Musica depois) {
        return !Objects.equals(normalizar(leitor.apply(antes)), normalizar(leitor.apply(depois)));
    }

    public void copiar(Musica origem, Musica destino) {
        copiador.accept(origem, destino);
    }

    public static List<CampoMusica> alterados(Musica antes, Musica depois) {
        return Arrays.stream(values())
                .filter(campo -> campo.mudou(antes, depois))
                .toList();
    }

    /**
     * Texto vazio e nulo sao a mesma ausencia para quem preenche o formulario -- e a tela manda
     * {@code ""} onde o banco tem {@code null} o tempo todo. Sem colapsar os dois, toda edicao
     * de qualquer campo pareceria ter mexido tambem em metade dos outros, e a pergunta sobre
     * refletir nos shows apareceria em salvamentos que nao mudaram nada.
     *
     * <p>Duracao tem a mesma historia com outra cara: o formulario manda {@code "00:00:00"} para
     * musica sem duracao, entao sem isso a primeira edicao de uma musica que nunca teve duracao
     * anunciaria uma alteracao de duracao que ninguem fez -- e ainda ofereceria grava-la nos
     * shows. A comparacao e pelo {@code toString()} porque {@link Time} guarda milissegundos
     * desde a epoca e o valor de meia-noite depende do fuso.
     */
    private static Object normalizar(Object valor) {
        if (valor instanceof String texto) {
            return texto.isBlank() ? null : texto;
        }
        if (valor instanceof Time duracao) {
            return "00:00:00".equals(duracao.toString()) ? null : duracao.toString();
        }
        return valor;
    }
}
