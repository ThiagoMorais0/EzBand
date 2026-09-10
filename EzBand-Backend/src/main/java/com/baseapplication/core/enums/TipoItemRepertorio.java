package com.baseapplication.core.enums;

/**
 * Natureza de um item do repertório do evento.
 *
 * <p>O repertório é uma sequência ordenada de itens, não uma lista de músicas: entre uma
 * música e outra a banda dá boa noite, para para beber água, fala do Instagram. Isso importa
 * na hora de preparar o show e de se situar no palco, e não importa nenhum na hora de olhar
 * quais músicas o show tem — daí os dois serem coisas distintas na mesma sequência.
 *
 * <p>Só {@link #MUSICA} vira faixa da sessão ao vivo. O ponteiro do Modo Palco continua
 * contando músicas, porque é assim que a banda fala ("vai pra 7"); o momento viaja pendurado
 * na música seguinte.
 */
public enum TipoItemRepertorio {
    MUSICA,
    MOMENTO
}
