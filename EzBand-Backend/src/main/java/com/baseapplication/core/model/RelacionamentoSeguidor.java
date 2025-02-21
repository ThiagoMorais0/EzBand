package com.baseapplication.core.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.baseapplication.core.enums.StatusSeguidor;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

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
