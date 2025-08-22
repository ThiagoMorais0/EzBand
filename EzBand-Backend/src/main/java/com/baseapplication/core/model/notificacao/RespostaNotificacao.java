package com.baseapplication.core.model.notificacao;

import com.baseapplication.core.enums.AcaoResposta;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "RESPOSTA_NOTIFICACAO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespostaNotificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notificacaoId;

    @Enumerated(EnumType.STRING)
    private AcaoResposta acao;

    private String mensagem;

    private LocalDateTime dataResposta = LocalDateTime.now();
}
