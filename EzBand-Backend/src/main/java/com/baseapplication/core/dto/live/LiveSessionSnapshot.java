package com.baseapplication.core.dto.live;

import com.baseapplication.core.enums.TipoEvento;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Estado autoritativo de uma sessão ao vivo.
 *
 * <p>O servidor é dono apenas do <b>ponteiro</b>: qual faixa a banda está tocando, quem
 * controla, quem está conectado e o log do que já passou. Metrônomo, auto-scroll, tamanho
 * de fonte e aba selecionada são de cada músico e nunca trafegam por aqui — é assim que o
 * requisito de autonomia individual vira arquitetura em vez de convenção.
 *
 * <p>Vive no {@code LiveSessionStore} (Redis em produção, memória em dev) com TTL, e
 * desce para o Postgres uma única vez no encerramento.
 */
@Getter
@Setter
@NoArgsConstructor
public class LiveSessionSnapshot implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idEvento;
    private TipoEvento tipoEvento;
    private Long idBanda;

    /** Contador monotônico. Toda mutação incrementa; o cliente usa para descartar mensagens fora de ordem. */
    private long seq;

    private Long iniciadaEm;
    private Long iniciadaPor;

    /** Última mutação. É por aqui que o varredor decide se a sala foi abandonada. */
    private Long atualizadaEm;

    /** Índice da faixa atual no repertório. {@code null} = a banda está na visão do setlist. */
    private Integer idxAtual;

    /** O repertório congelado na abertura da sessão. Carregado uma vez, nunca mais consultado. */
    private List<LiveFaixaInfo> faixas = new ArrayList<>();

    private List<Long> controladores = new ArrayList<>();

    /**
     * Instante (epoch ms) em que o último controlador desconectou.
     *
     * <p>Não limpamos {@code controladores} quando ele cai: se voltar em segundos, retoma
     * sem ninguém perceber. Passado o limiar, a UI de todos oferece assumir o controle.
     * Nada de reeleição automática — no palco a banda decide isso olhando pro lado.
     */
    private Long controleOrfaoDesde;

    private LiveTimerState timer = new LiveTimerState();

    private List<LiveFaixaTocada> tocadas = new ArrayList<>();

    public LiveSessionSnapshot(Long idEvento, TipoEvento tipoEvento, Long idBanda,
                               Long iniciadaPor, List<LiveFaixaInfo> faixas, long agora) {
        this.idEvento = idEvento;
        this.tipoEvento = tipoEvento;
        this.idBanda = idBanda;
        this.iniciadaPor = iniciadaPor;
        this.iniciadaEm = agora;
        this.atualizadaEm = agora;
        this.faixas = faixas != null ? faixas : new ArrayList<>();
        this.controladores.add(iniciadaPor);
    }

    /** Derivado — o cliente usa para desabilitar "próxima" na última faixa. */
    public int getTotalFaixas() {
        return faixas == null ? 0 : faixas.size();
    }

    public LiveFaixaInfo faixa(Integer idx) {
        if (idx == null || faixas == null || idx < 0 || idx >= faixas.size()) return null;
        return faixas.get(idx);
    }

    public boolean isControlador(Long idUsuario) {
        return idUsuario != null && controladores.contains(idUsuario);
    }

    /** A faixa aberta agora, se houver — a última entrada de {@link #tocadas} ainda sem {@code ate}. */
    public LiveFaixaTocada faixaEmAberto() {
        if (tocadas.isEmpty()) return null;
        LiveFaixaTocada ultima = tocadas.get(tocadas.size() - 1);
        return ultima.getAte() == null ? ultima : null;
    }
}
