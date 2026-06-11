package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CONFIGURACAO_NOTIFICACAO_USUARIO")
public class ConfiguracaoNotificacaoUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ID_USUARIO", nullable = false, unique = true)
    private Long idUsuario;

    @Column(name = "RECEBER_NOTIFICACOES_WHATSAPP")
    private Boolean receberNotificacoesWhatsapp = false;

    @ElementCollection
    @CollectionTable(
        name = "DIAS_ANTECEDENCIA_LEMBRETE_USUARIO",
        joinColumns = @JoinColumn(name = "ID_CONFIGURACAO")
    )
    @Column(name = "DIAS")
    private List<Integer> diasAntecedenciaLembrete = new ArrayList<>();

    // 1=Segunda, 2=Terça, ..., 7=Domingo, null=desativado
    @Column(name = "DIA_SEMANA_RESUMO_SEMANAL")
    private Integer diaSemanaResumoSemanal;
}
