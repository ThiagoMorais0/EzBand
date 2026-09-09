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

    /** Preenchidos quando a banda corrigiu o resumo à mão. */
    private LocalDateTime editadoEm;
    private Long editadoPor;
    private String editadoPorNome;

    /**
     * Se quem pediu pode corrigir este resumo.
     *
     * <p>Vem do servidor em vez de o cliente reimplementar a regra: a permissão mora num
     * lugar só, e a tela não precisa de uma segunda chamada para saber se mostra o botão.
     */
    private Boolean podeEditar;

    private List<FaixaDTO> faixas;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class FaixaDTO {
        private Long id;
        private Integer indice;
        private Integer ordem;
        private String titulo;
        private String artista;
        private LocalDateTime iniciadaEm;
        private Integer duracaoRealSegundos;
        private Integer duracaoCadastradaSegundos;
        private Boolean adicionadaManualmente;

        FaixaDTO(SessaoAoVivoFaixa entidade) {
            this.id = entidade.getId();
            this.indice = entidade.getIndice();
            this.ordem = entidade.getOrdem();
            this.titulo = entidade.getTitulo();
            this.artista = entidade.getArtista();
            this.iniciadaEm = entidade.getIniciadaEm();
            this.duracaoRealSegundos = entidade.getDuracaoRealSegundos();
            this.duracaoCadastradaSegundos = entidade.getDuracaoCadastradaSegundos();
            this.adicionadaManualmente = Boolean.TRUE.equals(entidade.getAdicionadaManualmente());
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

        this.editadoEm = sessao.getEditadoEm();
        this.editadoPor = sessao.getEditadoPor();

        // `ordem` manda; `iniciadaEm` é o desempate para sessões gravadas antes da edição
        // existir, que têm ordem nula em todas as linhas.
        this.faixas = sessao.getFaixas().stream()
                .sorted(Comparator
                        .comparing(SessaoAoVivoFaixa::getOrdem, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(SessaoAoVivoFaixa::getIniciadaEm, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(FaixaDTO::new)
                .toList();

        this.faixasTocadas = this.faixas.size();
        // Faixa acrescentada à mão não entra no desvio: a duração dela é lembrança, não
        // medição, e misturar as duas estragaria justamente o número que dá valor ao resumo.
        this.desvioSegundos = this.faixas.stream()
                .filter(f -> !Boolean.TRUE.equals(f.getAdicionadaManualmente()))
                .filter(f -> f.getDuracaoRealSegundos() != null && f.getDuracaoCadastradaSegundos() != null)
                .mapToInt(f -> f.getDuracaoRealSegundos() - f.getDuracaoCadastradaSegundos())
                .sum();
    }
}
