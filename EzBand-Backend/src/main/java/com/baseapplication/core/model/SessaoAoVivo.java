package com.baseapplication.core.model;

import com.baseapplication.core.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Resumo de uma sessão de palco já encerrada.
 *
 * <p>Gravado <b>uma única vez</b>, no encerramento — nunca a cada troca de música. Durante o
 * show o estado vive no Redis; só o resultado desce para o banco.
 *
 * <p>É o que transforma o Modo Performance em dado: as Métricas da banda ganham o que foi
 * realmente tocado, e a Sugestão de repertório passa a comparar a duração cadastrada com a
 * duração medida no palco.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "SESSAO_AO_VIVO")
public class SessaoAoVivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_evento", nullable = false)
    private Long idEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false)
    private TipoEvento tipoEvento;

    @Column(name = "id_banda")
    private Long idBanda;

    @Column(name = "id_usuario_iniciou")
    private Long idUsuarioIniciou;

    @Column(name = "iniciada_em")
    private LocalDateTime iniciadaEm;

    @Column(name = "finalizada_em")
    private LocalDateTime finalizadaEm;

    /** Quantas faixas o repertório tinha quando a sessão abriu. */
    @Column(name = "total_faixas_repertorio")
    private Integer totalFaixasRepertorio;

    /**
     * Quando alguém corrigiu o resumo à mão, e quem.
     *
     * <p>Só o usuário sabe se o registro automático bate com o que aconteceu no palco — um
     * clique errado para voltar uma música vira uma repetição que nunca houve. Por isso a
     * edição existe; e por isso ela deixa rastro, para quem lê o histórico depois saber que
     * está vendo uma versão corrigida.
     */
    @Column(name = "editado_em")
    private LocalDateTime editadoEm;

    @Column(name = "editado_por")
    private Long editadoPor;

    @OneToMany(mappedBy = "sessao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SessaoAoVivoFaixa> faixas = new ArrayList<>();

    public void adicionarFaixa(SessaoAoVivoFaixa faixa) {
        faixa.setSessao(this);
        this.faixas.add(faixa);
    }
}
