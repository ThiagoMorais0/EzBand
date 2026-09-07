package com.baseapplication.core.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Convite gerado para alguém que ainda NÃO possui conta no EzBand.
 * O token viaja no link de cadastro e é resgatado assim que a pessoa entra pela primeira vez.
 */
@Entity
@Table(name = "CONVITE_EXTERNO_BANDA")
@Getter
@Setter
@NoArgsConstructor
public class ConviteExternoBanda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "ID_BANDA")
    private Banda banda;

    private Long idUsuarioRemetente;

    private String instrumento;

    /** Eventos em que o convidado entra ao aceitar, no formato "SHOW:12,ENSAIO:33". */
    @Column(length = 1000)
    private String eventos;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    private LocalDateTime dataExpiracao;

    private Long idUsuarioAceitou;

    private LocalDateTime dataAceite;

    public boolean isExpirado() {
        return dataExpiracao != null && dataExpiracao.isBefore(LocalDateTime.now());
    }

    public boolean isUtilizado() {
        return idUsuarioAceitou != null;
    }
}
