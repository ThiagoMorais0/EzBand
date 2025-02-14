package com.baseapplication.core.model;

import com.baseapplication.core.enums.StatusSeguidor;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Entity
@Table(name = "NOTIFICACAO_USUARIO")
public class RelacionamentoSeguidor {

    @EmbeddedId
    private RelacionamentoSeguidorId id;

    private StatusSeguidor status;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataSolicitacao;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataAceitacao;
}
