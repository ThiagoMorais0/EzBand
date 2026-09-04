package com.baseapplication.core.dto.live;

import com.baseapplication.core.model.SessaoAoVivo;
import com.baseapplication.core.model.SessaoAoVivoFaixa;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/** O que a banda vê depois que o show acaba. */
@Getter
@Setter
@NoArgsConstructor
public class ResumoSessaoDTO {

    private Long id;
    private LocalDateTime iniciadaEm;
    private LocalDateTime finalizadaEm;
    private Integer duracaoTotalSegundos;
    private Integer totalFaixasRepertorio;
    private Integer faixasTocadas;
    /** Real menos cadastrado, somando as faixas que têm as duas medidas. Negativo = adiantou. */
    private Integer desvioSegundos;
    private List<FaixaDTO> faixas;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FaixaDTO {
        private Integer indice;
        private String titulo;
        private String artista;
        private LocalDateTime iniciadaEm;
        private Integer duracaoRealSegundos;
        private Integer duracaoCadastradaSegundos;

        FaixaDTO(SessaoAoVivoFaixa entidade) {
            this.indice = entidade.getIndice();
            this.titulo = entidade.getTitulo();
            this.artista = entidade.getArtista();
            this.iniciadaEm = entidade.getIniciadaEm();
            this.duracaoRealSegundos = entidade.getDuracaoRealSegundos();
            this.duracaoCadastradaSegundos = entidade.getDuracaoCadastradaSegundos();
        }
    }

    public ResumoSessaoDTO(SessaoAoVivo sessao) {
        this.id = sessao.getId();
        this.iniciadaEm = sessao.getIniciadaEm();
        this.finalizadaEm = sessao.getFinalizadaEm();
        this.totalFaixasRepertorio = sessao.getTotalFaixasRepertorio();

        if (sessao.getIniciadaEm() != null && sessao.getFinalizadaEm() != null) {
            this.duracaoTotalSegundos = (int) Duration.between(sessao.getIniciadaEm(), sessao.getFinalizadaEm()).getSeconds();
        }

        this.faixas = sessao.getFaixas().stream()
                .sorted(Comparator.comparing(SessaoAoVivoFaixa::getIniciadaEm,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .map(FaixaDTO::new)
                .toList();

        this.faixasTocadas = this.faixas.size();
        this.desvioSegundos = this.faixas.stream()
                .filter(f -> f.getDuracaoRealSegundos() != null && f.getDuracaoCadastradaSegundos() != null)
                .mapToInt(f -> f.getDuracaoRealSegundos() - f.getDuracaoCadastradaSegundos())
                .sum();
    }
}
