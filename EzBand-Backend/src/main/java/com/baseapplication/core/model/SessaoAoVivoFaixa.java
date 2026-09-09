package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Uma faixa efetivamente aberta durante a sessão de palco.
 *
 * <p>Só existe linha para faixa que a banda abriu. O que ficou de fora sai da diferença com
 * {@link SessaoAoVivo#getTotalFaixasRepertorio()} — não vale gravar linha vazia para música
 * que ninguém tocou.
 *
 * <p>Título e artista são cópia do momento: o repertório do evento pode ser editado depois, e
 * o histórico do show não pode mudar por causa disso.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SESSAO_AO_VIVO_FAIXA")
public class SessaoAoVivoFaixa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sessao")
    private SessaoAoVivo sessao;

    private Integer indice;
    private String titulo;
    private String artista;

    /**
     * Posição no resumo, editável pela banda.
     *
     * <p>Separada de {@link #iniciadaEm} de propósito: reordenar o resumo não pode reescrever
     * a hora em que a música de fato aconteceu. Nula em sessões gravadas antes deste campo
     * existir — nesse caso a ordem cai de volta para o horário de início.
     */
    private Integer ordem;

    /**
     * Faixa que a banda tocou mas o app não registrou, acrescentada à mão depois do show.
     *
     * <p>Não tem {@code iniciadaEm} nem duração medida: ninguém abriu ela na tela. Existe
     * para o resumo poder contar a verdade, e é marcada para que a métrica de desvio não
     * confunda uma estimativa humana com um tempo cronometrado.
     */
    @Column(name = "adicionada_manualmente")
    private Boolean adicionadaManualmente;

    @Column(name = "iniciada_em")
    private LocalDateTime iniciadaEm;

    @Column(name = "finalizada_em")
    private LocalDateTime finalizadaEm;

    /** Tempo real que a faixa passou na tela da banda. */
    @Column(name = "duracao_real_segundos")
    private Integer duracaoRealSegundos;

    /** Duração cadastrada no repertório, para comparar com a de cima. */
    @Column(name = "duracao_cadastrada_segundos")
    private Integer duracaoCadastradaSegundos;
}
