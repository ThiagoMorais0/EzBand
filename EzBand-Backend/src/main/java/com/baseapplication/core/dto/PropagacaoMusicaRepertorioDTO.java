package com.baseapplication.core.dto;

import java.util.ArrayList;
import java.util.List;

import com.baseapplication.core.enums.CampoMusica;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * O convite para levar uma edicao do repertorio da banda aos shows que ainda vao acontecer.
 *
 * <p>Volta na resposta de {@code atualizarMusicaRepertorio} quando a edicao mexeu em algo que
 * tambem existe na linha do evento e a musica esta em pelo menos um show pendente -- so entao a
 * tela pergunta. Se a resposta for sim, o mesmo objeto volta para o servidor em
 * {@code propagarMusicaParaShowsPendentes}.
 *
 * <p>O titulo e o artista anteriores viajam de proposito: a linha do evento nao guarda FK nenhuma
 * para a linha da banda, o vinculo entre as duas e a
 * {@link com.baseapplication.core.utils.ChaveMusica}, derivada de titulo + artista. Depois do
 * save a banda ja tem os valores novos, entao sem carregar os antigos nao haveria como
 * reencontrar a musica no setlist do show quando ela foi justamente renomeada.
 *
 * <p>Nada aqui e usado como valor: {@code campos} so diz quais campos copiar e os dados saem
 * sempre da linha do repertorio da banda, ja salva. Adulterar o payload, para um membro da
 * banda, nao alcanca nada que a propria tela nao faca.
 */
@Getter
@Setter
@NoArgsConstructor
public class PropagacaoMusicaRepertorioDTO {

    /** Id da linha de REPERTORIO_BANDA que acabou de ser editada. */
    private Long id;
    private Long idBanda;
    private String tituloAnterior;
    private String artistaAnterior;
    private List<CampoMusica> campos = new ArrayList<>();

    /**
     * Shows pendentes que tem a musica no setlist -- so para a tela listar na pergunta. O objeto
     * volta inteiro do navegador na confirmacao, e READ_ONLY deixa explicito que essa parte e
     * ignorada na volta: a propagacao releva os shows pendentes de novo, ja que um deles pode ter
     * virado realizado entre a pergunta e a resposta.
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<EventoPendenteConviteDTO> shows = new ArrayList<>();
}
