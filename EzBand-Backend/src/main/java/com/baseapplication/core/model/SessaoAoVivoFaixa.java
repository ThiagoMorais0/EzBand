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
