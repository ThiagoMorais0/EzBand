package com.baseapplication.core.dto.live;

import com.baseapplication.core.enums.LiveMessageType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Mensagem enviada do servidor para os clientes da sessão.
 *
 * <p>Envelope achatado e com {@code NON_NULL}: o cliente lê {@code msg.idx} direto, sem
 * descer num campo {@code dados}. Use as fábricas estáticas em vez do setter a seco — elas
 * são a documentação viva de quais campos cada tipo de mensagem carrega.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LiveServerMessage {

    private LiveMessageType tipo;
    private Long seq;
    private Long emEpochMs;

    private Integer idx;
    private Long porIdUsuario;

    private LiveSessionSnapshot estado;
    private List<LiveMembroPresenca> presenca;

    private LiveMembroPresenca membro;
    private Boolean entrou;

    private List<Long> controladores;
    private Long controleOrfaoDesde;

    private LiveTimerState timer;

    private String cueTipo;
    private String cueTexto;
    private String deNome;

    private Long idResumo;

    private String codigo;
    private String mensagem;

    private static LiveServerMessage base(LiveMessageType tipo) {
        LiveServerMessage m = new LiveServerMessage();
        m.tipo = tipo;
        m.emEpochMs = System.currentTimeMillis();
        return m;
    }

    public static LiveServerMessage sessionState(LiveSessionSnapshot estado, List<LiveMembroPresenca> presenca) {
        LiveServerMessage m = base(LiveMessageType.SESSION_STATE);
        m.seq = estado.getSeq();
        m.estado = estado;
        m.presenca = presenca;
        return m;
    }

    public static LiveServerMessage trackChanged(LiveSessionSnapshot estado, Long porIdUsuario) {
        LiveServerMessage m = base(LiveMessageType.TRACK_CHANGED);
        m.seq = estado.getSeq();
        m.idx = estado.getIdxAtual();
        m.porIdUsuario = porIdUsuario;
        return m;
    }

    public static LiveServerMessage presenceChanged(long seq, LiveMembroPresenca membro, boolean entrou,
                                                    List<LiveMembroPresenca> presenca) {
        LiveServerMessage m = base(LiveMessageType.PRESENCE_CHANGED);
        m.seq = seq;
        m.membro = membro;
        m.entrou = entrou;
        m.presenca = presenca;
        return m;
    }

    public static LiveServerMessage controllerChanged(LiveSessionSnapshot estado, Long porIdUsuario) {
        LiveServerMessage m = base(LiveMessageType.CONTROLLER_CHANGED);
        m.seq = estado.getSeq();
        m.controladores = estado.getControladores();
        m.controleOrfaoDesde = estado.getControleOrfaoDesde();
        m.porIdUsuario = porIdUsuario;
        return m;
    }

    public static LiveServerMessage timerChanged(LiveSessionSnapshot estado, Long porIdUsuario) {
        LiveServerMessage m = base(LiveMessageType.TIMER_CHANGED);
        m.seq = estado.getSeq();
        m.timer = estado.getTimer();
        m.porIdUsuario = porIdUsuario;
        return m;
    }

    /** Efêmero: não carrega seq porque não muta o estado da sessão. */
    public static LiveServerMessage cue(Long deIdUsuario, String deNome, String cueTipo, String cueTexto) {
        LiveServerMessage m = base(LiveMessageType.CUE);
        m.porIdUsuario = deIdUsuario;
        m.deNome = deNome;
        m.cueTipo = cueTipo;
        m.cueTexto = cueTexto;
        return m;
    }

    public static LiveServerMessage sessionEnded(Long idResumo, Long porIdUsuario) {
        LiveServerMessage m = base(LiveMessageType.SESSION_ENDED);
        m.idResumo = idResumo;
        m.porIdUsuario = porIdUsuario;
        return m;
    }

    public static LiveServerMessage erro(String codigo, String mensagem) {
        LiveServerMessage m = base(LiveMessageType.ERROR);
        m.codigo = codigo;
        m.mensagem = mensagem;
        return m;
    }
}
